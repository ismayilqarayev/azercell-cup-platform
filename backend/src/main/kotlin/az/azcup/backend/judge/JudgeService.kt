package az.azcup.backend.judge

import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.SubmissionStatus
import az.azcup.backend.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Comparator
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Şagirdin göndərdiyi C++ kodunu compile edib, problemin nümunə giriş/çıxışına
 * qarşı icra edən mərkəzi servis — platformanın "judge" (yoxlayıcı) hissəsi.
 *
 * DİQQƏT (təhlükəsizlik): burada konteyner (Docker və s.) izolyasiyası
 * İSTİFADƏ OLUNMUR — compile olunan proqram sərbəst bir OS prosesi kimi,
 * tətbiqin öz istifadəçi hüquqları altında icra olunur. Yalnız vaxt limiti
 * (timeout) və çıxış ölçüsü limiti (max-output-bytes) ilə məhdudlaşdırılır.
 * Bu, LOKAL/ETİBARLI istifadə üçün qəbul edilə bilər, amma açıq internetə
 * çıxarılan production mühitdə icra addımı mütləq konteynerləşdirilmiş
 * (sandboxed) bir alternativlə əvəz olunmalıdır (bax: runAgainstInput/runNoComparison).
 */
@Service
class JudgeService(
    // g++ kompilyatorunun yolu (adətən sadəcə "g++", PATH-də olduğu güman edilir).
    @param:Value("\${app.judge.gpp-path}") private val gppPath: String,
    // Hər yoxlama üçün müvəqqəti fayllar (main.cpp, main.exe) buraya yazılır.
    @param:Value("\${app.judge.workspace-dir}") workspaceDirPath: String,
    @param:Value("\${app.judge.compile-timeout-seconds}") private val compileTimeoutSeconds: Long,
    @param:Value("\${app.judge.run-timeout-seconds}") private val runTimeoutSeconds: Long,
    // Çox uzun kodların server resurslarını tükətməsinin qarşısını almaq üçün limit.
    @param:Value("\${app.judge.max-source-length}") private val maxSourceLength: Int,
    // Sonsuz dövrdə çap edən proqramların yaddaşı doldurmasının qarşısını alır.
    @param:Value("\${app.judge.max-output-bytes}") private val maxOutputBytes: Int
) {
    private val log = LoggerFactory.getLogger(JudgeService::class.java)
    private val workspaceDir: Path = Paths.get(workspaceDirPath)

    // Servis yaradılanda (Spring tərəfindən) iş qovluğunun mövcud olduğuna əmin ol.
    init {
        Files.createDirectories(workspaceDir)
    }

    // Konkret bir PROBLEMƏ qarşı yoxlama — SubmissionService bunu çağırır.
    // Nəticə problem.exampleOutput ilə müqayisə edilərək ACCEPTED/WRONG_ANSWER
    // qərarı verir (bax: runAgainstInput).
    fun judge(sourceCode: String?, problem: Problem): JudgeResult {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Kod boş ola bilməz")
        }
        if (sourceCode.length > maxSourceLength) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Kod çox uzundur (maksimum $maxSourceLength simvol)")
        }

        // Hər yoxlama üçün TƏSADÜFİ AD-lı ayrıca qovluq — paralel sorğular
        // bir-birinin fayllarını üst-üstə yazmasın deyə (izolyasiya).
        val workDir = workspaceDir.resolve(UUID.randomUUID().toString())
        try {
            Files.createDirectories(workDir)
            val sourceFile = workDir.resolve("main.cpp")
            Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8)
            val binary = workDir.resolve("main.exe")

            val compileOutcome = compile(sourceFile, binary, workDir)
            if (!compileOutcome.success) {
                return JudgeResult(SubmissionStatus.COMPILE_ERROR, "", compileOutcome.stderr, 0)
            }

            return runAgainstInput(binary, workDir, problem.exampleInput, problem.exampleOutput)
        } catch (e: IOException) {
            log.error("Judge I/O error", e)
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Yoxlama zamanı server xətası baş verdi")
        } finally {
            // Uğurlu/uğursuz — istənilən halda müvəqqəti faylları TƏMİZLƏ,
            // əks halda disk zamanla dolar.
            deleteRecursively(workDir)
        }
    }

    /**
     * İxtiyari kodu, çağıranın verdiyi stdin-ə qarşı compile edib icra edir —
     * gözlənilən çıxışla MÜQAYİSƏ YOXDUR. "Kod yazma sahəsi" (sərbəst
     * scratchpad) üçün istifadə olunur, çünki orada arxada konkret bir
     * [Problem] yoxdur, sadəcə "kodu işə sal və nəticəni göstər" tələb olunur.
     */
    fun runFree(sourceCode: String?, stdin: String?): JudgeResult {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Kod boş ola bilməz")
        }
        if (sourceCode.length > maxSourceLength) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Kod çox uzundur (maksimum $maxSourceLength simvol)")
        }

        val workDir = workspaceDir.resolve(UUID.randomUUID().toString())
        try {
            Files.createDirectories(workDir)
            val sourceFile = workDir.resolve("main.cpp")
            Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8)
            val binary = workDir.resolve("main.exe")

            val compileOutcome = compile(sourceFile, binary, workDir)
            if (!compileOutcome.success) {
                return JudgeResult(SubmissionStatus.COMPILE_ERROR, "", compileOutcome.stderr, 0)
            }

            return runNoComparison(binary, workDir, stdin)
        } catch (e: IOException) {
            log.error("Judge I/O error", e)
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "İcra zamanı server xətası baş verdi")
        } finally {
            deleteRecursively(workDir)
        }
    }

    // compile() metodunun nəticəsini daşıyan kiçik köməkçi tip.
    private data class CompileOutcome(val success: Boolean, val stderr: String)

    // g++ ilə mənbə faylını binar-a çevirir. -O2 (optimallaşdırma) və
    // -std=c++17 (dil standartı) bayraqları istifadə olunur.
    private fun compile(sourceFile: Path, binary: Path, workDir: Path): CompileOutcome {
        val pb = ProcessBuilder(
            gppPath, "-O2", "-std=c++17", "-o", binary.fileName.toString(), sourceFile.fileName.toString()
        )
        pb.directory(workDir.toFile())
        val process = pb.start()

        // stdout/stderr oxunuşu ayrı thread-lərdə aparılır ki, proses öz
        // çıxış buferini doldurub "asılı" qalmasın (klassik ProcessBuilder tələsi:
        // əgər stdout/stderr oxunmasa, kiçik OS buferi dolur və proses özü bloklanır).
        val stderrReader = CappedStreamReader.start(process.errorStream, maxOutputBytes)
        // g++ stdin gözləmir, ona görə giriş axınını dərhal bağlayırıq.
        process.outputStream.close()
        val stdoutReader = CappedStreamReader.start(process.inputStream, maxOutputBytes)

        val finished = waitQuietly(process, compileTimeoutSeconds)
        if (!finished) {
            process.destroyForcibly()
            return CompileOutcome(false, "Kompilyasiya vaxtı bitdi (timeout)")
        }
        stdoutReader.join()
        stderrReader.join()
        if (process.exitValue() != 0) {
            // g++ xəta mesajlarını adətən stderr-ə yazır.
            return CompileOutcome(false, stderrReader.result())
        }
        return CompileOutcome(true, "")
    }

    // Binar-ı verilmiş girişlə (stdin) icra edir və çıxışı problemin
    // gözlənilən nəticəsi (expectedOutput) ilə müqayisə edərək
    // ACCEPTED/WRONG_ANSWER qərarı verir.
    private fun runAgainstInput(binary: Path, workDir: Path, input: String?, expectedOutput: String?): JudgeResult {
        val pb = ProcessBuilder(binary.toAbsolutePath().toString())
        pb.directory(workDir.toFile())
        val process = pb.start()

        try {
            process.outputStream.use { stdin ->
                stdin.write((input ?: "").toByteArray(StandardCharsets.UTF_8))
            }
        } catch (ignored: IOException) {
            // Proqram bütün girişi oxumadan bitə bilər — bu xəta deyil.
        }

        val stdoutReader = CappedStreamReader.start(process.inputStream, maxOutputBytes)
        val stderrReader = CappedStreamReader.start(process.errorStream, maxOutputBytes)

        val start = System.currentTimeMillis()
        val finished = waitQuietly(process, runTimeoutSeconds)
        val elapsed = System.currentTimeMillis() - start

        if (!finished) {
            // Vaxt bitdi (məs. sonsuz dövr) — məcburi dayandır.
            process.destroyForcibly()
            return JudgeResult(SubmissionStatus.TIME_LIMIT_EXCEEDED, "", "", elapsed)
        }
        stdoutReader.join()
        stderrReader.join()

        val stdout = stdoutReader.result()
        val stderr = stderrReader.result()

        if (process.exitValue() != 0) {
            // Proqram sıfırdan fərqli kodla bitib — çöküş, seqmentasiya xətası və s.
            return JudgeResult(SubmissionStatus.RUNTIME_ERROR, stdout, stderr, elapsed)
        }

        // normalize() sətir sonu simvollarını (\r\n vs \n) və sondakı boş
        // sətirləri/boşluqları ehmal edərək müqayisə edir — beləliklə
        // "eyni məzmun, fərqli format" halları səhv WRONG_ANSWER kimi qiymətləndirilmir.
        val matches = normalize(stdout) == normalize(expectedOutput)
        val status = if (matches) SubmissionStatus.ACCEPTED else SubmissionStatus.WRONG_ANSWER
        return JudgeResult(status, stdout, stderr, elapsed)
    }

    /**
     * runAgainstInput ilə eynidir, sadəcə gözlənilən çıxışla MÜQAYİSƏ YOXDUR —
     * burada ACCEPTED sadəcə "proqram uğurla icra olundu, çıxış kodu 0-dır"
     * mənasını daşıyır (düzgün NƏTİCƏ demək deyil, yalnız uğurlu İCRA deməkdir).
     */
    private fun runNoComparison(binary: Path, workDir: Path, input: String?): JudgeResult {
        val pb = ProcessBuilder(binary.toAbsolutePath().toString())
        pb.directory(workDir.toFile())
        val process = pb.start()

        try {
            process.outputStream.use { stdin ->
                stdin.write((input ?: "").toByteArray(StandardCharsets.UTF_8))
            }
        } catch (ignored: IOException) {
            // Proqram bütün girişi oxumadan bitə bilər — bu xəta deyil.
        }

        val stdoutReader = CappedStreamReader.start(process.inputStream, maxOutputBytes)
        val stderrReader = CappedStreamReader.start(process.errorStream, maxOutputBytes)

        val start = System.currentTimeMillis()
        val finished = waitQuietly(process, runTimeoutSeconds)
        val elapsed = System.currentTimeMillis() - start

        if (!finished) {
            process.destroyForcibly()
            return JudgeResult(SubmissionStatus.TIME_LIMIT_EXCEEDED, "", "", elapsed)
        }
        stdoutReader.join()
        stderrReader.join()

        val stdout = stdoutReader.result()
        val stderr = stderrReader.result()

        if (process.exitValue() != 0) {
            return JudgeResult(SubmissionStatus.RUNTIME_ERROR, stdout, stderr, elapsed)
        }
        return JudgeResult(SubmissionStatus.ACCEPTED, stdout, stderr, elapsed)
    }

    // Bir prosesin stdout/stderr axınını FONDA (ayrı thread-də) oxuyub
    // yaddaşda toplayan köməkçi sinif. maxBytes limitindən sonra gələn
    // baytlar sadəcə atılır (proses yenə də icra olunmağa davam edir,
    // amma çıxışı artıq yaddaşa yazılmır — yaddaş tükənməsinin qarşısı alınır).
    private class CappedStreamReader private constructor(inStream: InputStream, maxBytes: Int) {
        private val thread: Thread = Thread {
            try {
                val buf = ByteArray(4096)
                val out = ByteArrayOutputStream()
                var n: Int
                while (inStream.read(buf).also { n = it } != -1) {
                    if (out.size() < maxBytes) {
                        out.write(buf, 0, minOf(n, maxBytes - out.size()))
                    }
                }
                result = out.toString(StandardCharsets.UTF_8)
            } catch (ignored: IOException) {
                // Proses məcburi dayandırıldığı üçün stream bağlanıb — normaldır.
            }
        }

        @Volatile
        private var result: String = ""

        init {
            // Daemon thread — əsas tətbiq bağlanarkən bu thread-lərin bitməsini
            // gözləməyə ehtiyac qalmır, JVM-i bloklamırlar.
            thread.isDaemon = true
        }

        // Thread-in bitməsini ən çoxu 2 saniyə gözləyir (uzun gözləməmək üçün
        // qısa timeout — proses artıq destroyForcibly() ilə öldürülübsə,
        // thread demək olar dərhal bitməlidir).
        fun join() {
            try {
                thread.join(2000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        fun result(): String = result

        companion object {
            fun start(inStream: InputStream, maxBytes: Int): CappedStreamReader {
                val reader = CappedStreamReader(inStream, maxBytes)
                reader.thread.start()
                return reader
            }
        }
    }

    companion object {
        // Prosesin bitməsini verilmiş saniyə qədər gözləyir; kəsilmə
        // (interrupt) halında prosesi məcburi öldürüb false qaytarır.
        private fun waitQuietly(process: Process, timeoutSeconds: Long): Boolean =
            try {
                process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                process.destroyForcibly()
                false
            }

        // İki mətni "praktik olaraq eyni" olub-olmadığını müəyyən etmək üçün
        // normallaşdırır: Windows/Unix sətir sonu fərqlərini (\r\n vs \n)
        // aradan qaldırır, hər sətrin sonundakı boşluqları və mətnin
        // sonundakı boş sətirləri atır. Bu olmasa, şagirdin düzgün cavabı
        // sadəcə əlavə boş sətirə görə WRONG_ANSWER ala bilərdi.
        private fun normalize(s: String?): String {
            if (s == null) return ""
            val lines = s.replace("\r\n", "\n").replace('\r', '\n').split("\n")
            var end = lines.size
            while (end > 0 && lines[end - 1].trim().isEmpty()) end--
            val sb = StringBuilder()
            for (i in 0 until end) {
                if (i > 0) sb.append('\n')
                sb.append(lines[i].trimEnd())
            }
            return sb.toString()
        }

        // Bir yoxlamaya aid müvəqqəti qovluğu (main.cpp, main.exe və s.) tam
        // silir. Fayllar əvvəlcə silinməlidir ki, boşalan qovluqları silmək
        // mümkün olsun — ona görə Comparator.reverseOrder() ilə "ən dərin"
        // yoldan başlanır (əvvəl fayllar, sonra qovluqlar).
        private fun deleteRecursively(dir: Path) {
            if (!Files.exists(dir)) return
            try {
                Files.walk(dir).use { walk ->
                    walk.sorted(Comparator.reverseOrder<Path>()).forEach { p ->
                        try {
                            Files.deleteIfExists(p)
                        } catch (ignored: IOException) {
                        }
                    }
                }
            } catch (ignored: IOException) {
            }
        }
    }
}

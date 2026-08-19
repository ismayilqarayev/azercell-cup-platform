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
 * Compiles and runs a student's C++ submission against a problem's sample I/O.
 *
 * No container sandbox is used here — the compiled binary runs as a plain OS
 * process under the app's own user, bounded only by a wall-clock kill-timer and
 * an output-size cap. Acceptable for local/trusted use; must not be exposed on
 * the open internet without swapping the run step for a containerized one
 * (see [runAgainstInput]).
 */
@Service
class JudgeService(
    @param:Value("\${app.judge.gpp-path}") private val gppPath: String,
    @param:Value("\${app.judge.workspace-dir}") workspaceDirPath: String,
    @param:Value("\${app.judge.compile-timeout-seconds}") private val compileTimeoutSeconds: Long,
    @param:Value("\${app.judge.run-timeout-seconds}") private val runTimeoutSeconds: Long,
    @param:Value("\${app.judge.max-source-length}") private val maxSourceLength: Int,
    @param:Value("\${app.judge.max-output-bytes}") private val maxOutputBytes: Int
) {
    private val log = LoggerFactory.getLogger(JudgeService::class.java)
    private val workspaceDir: Path = Paths.get(workspaceDirPath)

    init {
        Files.createDirectories(workspaceDir)
    }

    fun judge(sourceCode: String?, problem: Problem): JudgeResult {
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

            return runAgainstInput(binary, workDir, problem.exampleInput, problem.exampleOutput)
        } catch (e: IOException) {
            log.error("Judge I/O error", e)
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Yoxlama zamanı server xətası baş verdi")
        } finally {
            deleteRecursively(workDir)
        }
    }

    /**
     * Compiles and runs arbitrary source code against caller-supplied stdin,
     * with no expected-output comparison — used by the free-write scratchpad
     * ("Kod yazma sahəsi") where there is no backing [Problem].
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

    private data class CompileOutcome(val success: Boolean, val stderr: String)

    private fun compile(sourceFile: Path, binary: Path, workDir: Path): CompileOutcome {
        val pb = ProcessBuilder(
            gppPath, "-O2", "-std=c++17", "-o", binary.fileName.toString(), sourceFile.fileName.toString()
        )
        pb.directory(workDir.toFile())
        val process = pb.start()

        val stderrReader = CappedStreamReader.start(process.errorStream, maxOutputBytes)
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
            return CompileOutcome(false, stderrReader.result())
        }
        return CompileOutcome(true, "")
    }

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

        val matches = normalize(stdout) == normalize(expectedOutput)
        val status = if (matches) SubmissionStatus.ACCEPTED else SubmissionStatus.WRONG_ANSWER
        return JudgeResult(status, stdout, stderr, elapsed)
    }

    /** Same as [runAgainstInput] but with no expected-output comparison — ACCEPTED here just means "ran, exit code 0". */
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
            thread.isDaemon = true
        }

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
        private fun waitQuietly(process: Process, timeoutSeconds: Long): Boolean =
            try {
                process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                process.destroyForcibly()
                false
            }

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

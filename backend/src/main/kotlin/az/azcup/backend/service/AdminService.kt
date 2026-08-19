package az.azcup.backend.service

import az.azcup.backend.dto.admin.AdminProblemDto
import az.azcup.backend.dto.admin.AdminTopicDto
import az.azcup.backend.dto.admin.AdminUserDetailDto
import az.azcup.backend.dto.admin.AdminUserDto
import az.azcup.backend.dto.admin.ProblemUpsertRequest
import az.azcup.backend.dto.admin.TopicUpsertRequest
import az.azcup.backend.dto.admin.UserProfileUpdateRequest
import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.Role
import az.azcup.backend.entity.Topic
import az.azcup.backend.entity.User
import az.azcup.backend.exception.ConflictException
import az.azcup.backend.exception.NotFoundException
import az.azcup.backend.repository.ProblemRepository
import az.azcup.backend.repository.SubmissionRepository
import az.azcup.backend.repository.TopicRepository
import az.azcup.backend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom

// Admin panelinin bütün biznes-məntiqi: mövzu/problem CRUD-u və
// istifadəçi idarəetməsi (rol dəyişmə, aktiv/deaktiv, parol sıfırlama,
// silmə). Bir çox metodda "son admin"i və ya "özünü" korlamağın qarşısını
// alan qoruyucu yoxlamalar var — aşağıda hər birinin niyəsi izah olunur.
@Service
class AdminService(
    private val topicRepository: TopicRepository,
    private val problemRepository: ProblemRepository,
    private val userRepository: UserRepository,
    private val submissionRepository: SubmissionRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // Təsadüfi parol yaratmaq üçün — java.util.Random YOX, məhz
    // SecureRandom istifadə olunur, çünki adi Random kriptoqrafik cəhətdən
    // proqnozlaşdırıla bilər (parol yaratmaq kimi təhlükəsizlik-kritik
    // əməliyyatlar üçün yararsızdır).
    private val secureRandom = SecureRandom()

    @Transactional
    fun createTopic(req: TopicUpsertRequest): AdminTopicDto {
        if (topicRepository.findBySlug(req.slug) != null) {
            throw ConflictException("Bu slug artıq mövcuddur: ${req.slug}")
        }
        val topic = Topic(
            slug = req.slug,
            orderIndex = req.orderIndex ?: 0,
            title = req.title,
            monthTag = req.monthTag,
            description = req.description,
            published = req.published
        )
        return toDto(topicRepository.save(topic))
    }

    @Transactional
    fun updateTopic(id: Long, req: TopicUpsertRequest): AdminTopicDto {
        val topic = topicRepository.findById(id).orElseThrow { NotFoundException("Mövzu tapılmadı: $id") }
        topic.slug = req.slug
        topic.orderIndex = req.orderIndex ?: 0
        topic.title = req.title
        topic.monthTag = req.monthTag
        topic.description = req.description
        topic.published = req.published
        return toDto(topicRepository.save(topic))
    }

    @Transactional
    fun deleteTopic(id: Long) {
        if (!topicRepository.existsById(id)) {
            throw NotFoundException("Mövzu tapılmadı: $id")
        }
        // DİQQƏT: mövzuya aid problemlər/təqdimatlar burada əl ilə silinmir —
        // bazadakı FK (foreign key) ON DELETE davranışına etibar edilir.
        topicRepository.deleteById(id)
    }

    // Müəllim panelindəki "Dərs proqramı" tabı üçün: bütün mövzuları (açıq və
    // qapalı) sıra nömrəsinə görə qaytarır ki, müəllim hansının hələ tələbəyə
    // görünmədiyini görüb aça bilsin.
    @Transactional(readOnly = true)
    fun listTopicsForManagement(): List<AdminTopicDto> =
        topicRepository.findAllByOrderByOrderIndexAsc().map { toDto(it) }

    // Bir mövzunun "published" bayrağını dəyişir — istənilən TEACHER (və ya
    // ADMIN) bunu çağıra bilər (bax: TeacherController). Mövzu açıq elan
    // olunmayana qədər TopicService/ProblemService STUDENT roluna onu (və
    // ona aid məsələləri) göstərmir.
    @Transactional
    fun setTopicPublished(id: Long, published: Boolean): AdminTopicDto {
        val topic = topicRepository.findById(id).orElseThrow { NotFoundException("Mövzu tapılmadı: $id") }
        topic.published = published
        return toDto(topicRepository.save(topic))
    }

    @Transactional
    fun createProblem(req: ProblemUpsertRequest): AdminProblemDto {
        val topic = topicRepository.findBySlug(req.topicSlug)
            ?: throw NotFoundException("Mövzu tapılmadı: ${req.topicSlug}")
        val problem = Problem(
            topic = topic,
            orderIndex = req.orderIndex ?: 0,
            subgroupLabel = req.subgroupLabel,
            title = req.title,
            difficulty = req.difficulty,
            tags = (req.tags ?: emptyList()).toMutableList(),
            statement = req.statement,
            inputSpec = req.inputSpec,
            outputSpec = req.outputSpec,
            exampleInput = req.exampleInput ?: "",
            exampleOutput = req.exampleOutput,
            approach = req.approach,
            referenceSolution = req.referenceSolution
        )
        return toDto(problemRepository.save(problem))
    }

    @Transactional
    fun updateProblem(id: Long, req: ProblemUpsertRequest): AdminProblemDto {
        val problem = problemRepository.findById(id).orElseThrow { NotFoundException("Məsələ tapılmadı: $id") }
        val topic = topicRepository.findBySlug(req.topicSlug)
            ?: throw NotFoundException("Mövzu tapılmadı: ${req.topicSlug}")
        // Problemin mövzusu da dəyişdirilə bilər (məs. yanlış mövzuya
        // düşmüş problemi düzgün mövzuya köçürmək üçün).
        problem.topic = topic
        problem.orderIndex = req.orderIndex ?: 0
        problem.subgroupLabel = req.subgroupLabel
        problem.title = req.title
        problem.difficulty = req.difficulty
        problem.tags = (req.tags ?: emptyList()).toMutableList()
        problem.statement = req.statement
        problem.inputSpec = req.inputSpec
        problem.outputSpec = req.outputSpec
        problem.exampleInput = req.exampleInput ?: ""
        problem.exampleOutput = req.exampleOutput
        problem.approach = req.approach
        problem.referenceSolution = req.referenceSolution
        return toDto(problemRepository.save(problem))
    }

    @Transactional
    fun deleteProblem(id: Long) {
        if (!problemRepository.existsById(id)) {
            throw NotFoundException("Məsələ tapılmadı: $id")
        }
        problemRepository.deleteById(id)
    }

    // Entity -> DTO çevirmə köməkçiləri. Kotlin-in overload dəstəyi
    // sayəsində eyni "toDto" adı Problem və Topic üçün ayrıca istifadə
    // oluna bilir (parametr tipinə görə fərqləndirilir).
    private fun toDto(t: Topic): AdminTopicDto =
        AdminTopicDto(t.id, t.slug, t.orderIndex, t.title, t.monthTag, t.description, t.published)

    private fun toDto(p: Problem): AdminProblemDto =
        AdminProblemDto(
            p.id, p.topic!!.slug, p.orderIndex, p.subgroupLabel, p.title,
            p.difficulty, p.tags, p.statement, p.inputSpec, p.outputSpec,
            p.exampleInput, p.exampleOutput, p.approach, p.referenceSolution
        )

    // Admin panelindəki şagird siyahısı — hər şagirdin cəmi neçə fərqli
    // problem həll etdiyi də (totalSolved) birlikdə hesablanır.
    @Transactional(readOnly = true)
    fun listStudents(): List<AdminUserDto> =
        userRepository.findAllByRole(Role.STUDENT).map { u ->
            AdminUserDto(
                u.id, u.fullName, u.email, u.createdAt,
                submissionRepository.countDistinctSolvedProblems(u)
            )
        }

    // Hələ təsdiqlənməmiş müəllim hesabları — admin panelinin "Müəllim
    // təsdiqləri" ekranı bunu göstərir.
    @Transactional(readOnly = true)
    fun listPendingTeachers(): List<AdminUserDto> =
        userRepository.findAllByRole(Role.TEACHER)
            .filter { !it.approved }
            .map { u -> AdminUserDto(u.id, u.fullName, u.email, u.createdAt, 0L) }

    @Transactional
    fun approveTeacher(id: Long) {
        val user = userRepository.findById(id).orElseThrow { NotFoundException("Müəllim tapılmadı: $id") }
        if (user.role != Role.TEACHER) {
            throw ConflictException("Bu istifadəçi müəllim deyil")
        }
        user.approved = true
        userRepository.save(user)
    }

    // "Rədd et" — hesabı sadəcə deaktiv etmək əvəzinə TAMAMILƏ SİLİR, çünki
    // hələ təsdiqlənməmiş, real istifadə olunmayan bir qeydiyyat cəhdidir.
    @Transactional
    fun rejectTeacher(id: Long) {
        val user = userRepository.findById(id).orElseThrow { NotFoundException("Müəllim tapılmadı: $id") }
        if (user.role != Role.TEACHER) {
            throw ConflictException("Bu istifadəçi müəllim deyil")
        }
        userRepository.delete(user)
    }

    @Transactional(readOnly = true)
    fun listAllUsers(): List<AdminUserDetailDto> =
        userRepository.findAll().map { toDetailDto(it) }

    @Transactional(readOnly = true)
    fun getUser(id: Long): AdminUserDetailDto = toDetailDto(findUserOrThrow(id))

    @Transactional
    fun updateProfile(id: Long, req: UserProfileUpdateRequest): AdminUserDetailDto {
        val user = findUserOrThrow(id)
        // E-poçt YALNIZ dəyişəndə unikallıq yoxlanılır (ignoreCase ilə) —
        // əks halda istifadəçi öz mövcud e-poçtunu "təkrar göndərəndə" belə
        // "artıq istifadə olunur" xətası alardı.
        if (!user.email.equals(req.email, ignoreCase = true) && userRepository.existsByEmail(req.email)) {
            throw ConflictException("Bu email artıq başqa istifadəçi tərəfindən istifadə olunur")
        }
        user.fullName = req.fullName
        user.email = req.email
        return toDetailDto(userRepository.save(user))
    }

    // Bir istifadəçinin rolunu dəyişir. currentAdminId — bunu çağıran adminin
    // ID-sidir (özünü redaktə etməsinin qarşısını almaq üçün).
    @Transactional
    fun changeRole(id: Long, newRole: Role, currentAdminId: Long): AdminUserDetailDto {
        val user = findUserOrThrow(id)
        // Admin öz rolunu dəyişə bilməz — əks halda diqqətsizlikdən özünü
        // sistemdən "kilidləyə" bilərdi (heç kimin admin qalmadığı vəziyyət).
        if (user.id == currentAdminId) {
            throw ConflictException("Öz rolunu dəyişə bilməzsən")
        }
        // Sistemdə YALNIZ 1 admin qalıbsa, onun rolunu dəyişmək qadağandır —
        // bu, "heç bir admin qalmayan" bərpa olunmaz vəziyyətin qarşısını alır.
        if (user.role == Role.ADMIN && newRole != Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw ConflictException("Sistemdəki son admin hesabının rolu dəyişdirilə bilməz")
        }
        user.role = newRole
        // TEACHER-dən başqa hər rola keçidlə birlikdə avtomatik təsdiqlənir
        // (STUDENT/ADMIN-in "approved" statusu mənasızdır, yalnız TEACHER üçün əhəmiyyətlidir).
        if (newRole != Role.TEACHER) {
            user.approved = true
        }
        return toDetailDto(userRepository.save(user))
    }

    // Bir istifadəçini aktiv/deaktiv edir — silmək əvəzinə, tarixçəni saxlayaraq
    // girişini bloklamaq üçün istifadə olunur.
    @Transactional
    fun setActive(id: Long, active: Boolean, currentAdminId: Long): AdminUserDetailDto {
        val user = findUserOrThrow(id)
        // Admin özünü deaktiv edərək özünü sistemdən çıxara bilməz.
        if (user.id == currentAdminId && !active) {
            throw ConflictException("Öz hesabını deaktiv edə bilməzsən")
        }
        // Son (yeganə) admini deaktiv etmək də eyni səbəbdən qadağandır
        // (bax: changeRole-dakı oxşar qoruma).
        if (!active && user.role == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw ConflictException("Sistemdəki son admin hesabı deaktiv edilə bilməz")
        }
        user.active = active
        return toDetailDto(userRepository.save(user))
    }

    // Admin bir istifadəçinin parolunu sıfırlayır — ya öz istədiyi yeni
    // parolu təyin edə bilər, ya da boş buraxaraq təsadüfi güclü parol
    // yaratdıra bilər (bax: generateRandomPassword).
    @Transactional
    fun resetPassword(id: Long, requestedPassword: String?): String {
        val user = findUserOrThrow(id)
        val newPassword = if (requestedPassword.isNullOrBlank()) generateRandomPassword() else requestedPassword
        user.passwordHash = passwordEncoder.encode(newPassword)!!
        userRepository.save(user)
        // Açıq mətn parol YALNIZ bir dəfəlik, bu cavabda qaytarılır —
        // bazada heç vaxt saxlanılmır (yalnız hash-i saxlanılır).
        return newPassword
    }

    @Transactional
    fun deleteUser(id: Long, currentAdminId: Long) {
        val user = findUserOrThrow(id)
        // Admin özünü silə bilməz (yenə eyni "sistemi kilidləməmə" məntiqi).
        if (user.id == currentAdminId) {
            throw ConflictException("Öz hesabını silə bilməzsən")
        }
        if (user.role == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw ConflictException("Sistemdəki son admin hesabı silinə bilməz")
        }
        userRepository.delete(user)
    }

    private fun findUserOrThrow(id: Long): User =
        userRepository.findById(id).orElseThrow { NotFoundException("İstifadəçi tapılmadı: $id") }

    // Kriptoqrafik cəhətdən təhlükəsiz təsadüfi parol yaradır. Qarışdırıcı
    // ola bilən simvollar (böyük I/O, kiçik l, rəqəm 0/1) PASSWORD_CHARS
    // siyahısından bilərəkdən çıxarılıb ki, admin parolu əl ilə köçürəndə səhv etməsin.
    private fun generateRandomPassword(): String {
        val sb = StringBuilder(GENERATED_PASSWORD_LENGTH)
        repeat(GENERATED_PASSWORD_LENGTH) {
            sb.append(PASSWORD_CHARS[secureRandom.nextInt(PASSWORD_CHARS.length)])
        }
        return sb.toString()
    }

    // Yalnız STUDENT rolunda "həll edilmiş problem sayı" mənalıdır —
    // TEACHER/ADMIN üçün bu sahə həmişə 0 qaytarılır (əlavə, faydasız
    // sorğu aparmamaq üçün).
    private fun toDetailDto(u: User): AdminUserDetailDto {
        val solved = if (u.role == Role.STUDENT) submissionRepository.countDistinctSolvedProblems(u) else 0L
        return AdminUserDetailDto(
            u.id, u.fullName, u.email, u.role,
            u.approved, u.active, u.createdAt, solved
        )
    }

    companion object {
        // Böyük I/O, kiçik l və rəqəm 0/1 kimi vizual cəhətdən qarışdırıla
        // bilən simvollar BİLƏRƏKDƏN çıxarılıb (bax: generateRandomPassword).
        private const val PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
        private const val GENERATED_PASSWORD_LENGTH = 14
    }
}

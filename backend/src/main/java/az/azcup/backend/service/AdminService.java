package az.azcup.backend.service;

import az.azcup.backend.dto.admin.AdminProblemDto;
import az.azcup.backend.dto.admin.AdminTopicDto;
import az.azcup.backend.dto.admin.AdminUserDetailDto;
import az.azcup.backend.dto.admin.AdminUserDto;
import az.azcup.backend.dto.admin.ProblemUpsertRequest;
import az.azcup.backend.dto.admin.TopicUpsertRequest;
import az.azcup.backend.dto.admin.UserProfileUpdateRequest;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ConflictException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.TopicRepository;
import az.azcup.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

// Admin panelinin bütün biznes-məntiqi: mövzu/problem CRUD-u və
// istifadəçi idarəetməsi (rol dəyişmə, aktiv/deaktiv, parol sıfırlama,
// silmə). Bir çox metodda "son admin"i və ya "özünü" korlamağın qarşısını
// alan qoruyucu yoxlamalar var — aşağıda hər birinin niyəsi izah olunur.
@Service
public class AdminService {

    // Böyük I/O, kiçik l və rəqəm 0/1 kimi vizual cəhətdən qarışdırıla
    // bilən simvollar BİLƏRƏKDƏN çıxarılıb (bax: generateRandomPassword).
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int GENERATED_PASSWORD_LENGTH = 14;

    // Mövzuların CRUD əməliyyatları üçün.
    private final TopicRepository topicRepository;
    // Problemlərin CRUD əməliyyatları üçün.
    private final ProblemRepository problemRepository;
    // İstifadəçilərin oxunması/yazılması üçün.
    private final UserRepository userRepository;
    // İstifadəçinin həll etdiyi problem sayını hesablamaq üçün.
    private final SubmissionRepository submissionRepository;
    // Yeni/sıfırlanmış parolları bazaya yazmazdan əvvəl hash-ləmək üçün.
    private final PasswordEncoder passwordEncoder;

    // Təsadüfi parol yaratmaq üçün — java.util.Random YOX, məhz
    // SecureRandom istifadə olunur, çünki adi Random kriptoqrafik cəhətdən
    // proqnozlaşdırıla bilər (parol yaratmaq kimi təhlükəsizlik-kritik
    // əməliyyatlar üçün yararsızdır).
    private final SecureRandom secureRandom = new SecureRandom();

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public AdminService(
        TopicRepository topicRepository,
        ProblemRepository problemRepository,
        UserRepository userRepository,
        SubmissionRepository submissionRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Yeni mövzu yaradır; slug-un artıq mövcud olub-olmadığını əvvəlcədən yoxlayır.
    @Transactional
    public AdminTopicDto createTopic(TopicUpsertRequest req) {
        if (topicRepository.findBySlug(req.getSlug()) != null) {
            throw new ConflictException("Bu slug artıq mövcuddur: " + req.getSlug());
        }
        Topic topic = new Topic();
        topic.setSlug(req.getSlug());
        // orderIndex göndərilməyibsə (null), default olaraq 0 istifadə olunur.
        int orderIndex;
        if (req.getOrderIndex() != null) {
            orderIndex = req.getOrderIndex();
        } else {
            orderIndex = 0;
        }
        topic.setOrderIndex(orderIndex);
        topic.setTitle(req.getTitle());
        topic.setMonthTag(req.getMonthTag());
        topic.setDescription(req.getDescription());
        topic.setPublished(req.isPublished());
        return toDto(topicRepository.save(topic));
    }

    // Mövcud mövzunu ID-yə görə tapıb bütün sahələrini yeni dəyərlərlə əvəz edir.
    @Transactional
    public AdminTopicDto updateTopic(Long id, TopicUpsertRequest req) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new NotFoundException("Mövzu tapılmadı: " + id));
        topic.setSlug(req.getSlug());
        // orderIndex göndərilməyibsə (null), default olaraq 0 istifadə olunur.
        int orderIndex;
        if (req.getOrderIndex() != null) {
            orderIndex = req.getOrderIndex();
        } else {
            orderIndex = 0;
        }
        topic.setOrderIndex(orderIndex);
        topic.setTitle(req.getTitle());
        topic.setMonthTag(req.getMonthTag());
        topic.setDescription(req.getDescription());
        topic.setPublished(req.isPublished());
        return toDto(topicRepository.save(topic));
    }

    @Transactional
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new NotFoundException("Mövzu tapılmadı: " + id);
        }
        // DİQQƏT: mövzuya aid problemlər/təqdimatlar burada əl ilə silinmir —
        // bazadakı FK (foreign key) ON DELETE davranışına etibar edilir.
        topicRepository.deleteById(id);
    }

    // Müəllim panelindəki "Dərs proqramı" tabı üçün: bütün mövzuları (açıq və
    // qapalı) sıra nömrəsinə görə qaytarır ki, müəllim hansının hələ tələbəyə
    // görünmədiyini görüb aça bilsin.
    @Transactional(readOnly = true)
    public List<AdminTopicDto> listTopicsForManagement() {
        List<Topic> topics = topicRepository.findAllByOrderByOrderIndexAsc();
        // Hər Topic entity-sini admin DTO formasına çevirir.
        List<AdminTopicDto> result = new ArrayList<>();
        for (Topic topic : topics) {
            result.add(toDto(topic));
        }
        return result;
    }

    // Bir mövzunun "published" bayrağını dəyişir — istənilən TEACHER (və ya
    // ADMIN) bunu çağıra bilər (bax: TeacherController). Mövzu açıq elan
    // olunmayana qədər TopicService/ProblemService STUDENT roluna onu (və
    // ona aid məsələləri) göstərmir.
    @Transactional
    public AdminTopicDto setTopicPublished(Long id, boolean published) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new NotFoundException("Mövzu tapılmadı: " + id));
        topic.setPublished(published);
        return toDto(topicRepository.save(topic));
    }

    // Yeni problem yaradır; verilmiş topicSlug-a uyğun mövzunun mövcud olduğunu yoxlayır.
    @Transactional
    public AdminProblemDto createProblem(ProblemUpsertRequest req) {
        Topic topic = topicRepository.findBySlug(req.getTopicSlug());
        if (topic == null) {
            throw new NotFoundException("Mövzu tapılmadı: " + req.getTopicSlug());
        }
        Problem problem = new Problem();
        problem.setTopic(topic);
        // orderIndex göndərilməyibsə (null), default olaraq 0 istifadə olunur.
        int orderIndex;
        if (req.getOrderIndex() != null) {
            orderIndex = req.getOrderIndex();
        } else {
            orderIndex = 0;
        }
        problem.setOrderIndex(orderIndex);
        problem.setSubgroupLabel(req.getSubgroupLabel());
        problem.setTitle(req.getTitle());
        problem.setDifficulty(req.getDifficulty());
        // tags göndərilməyibsə (null), Problem.tags-in heç vaxt null olmaması
        // üçün boş siyahı istifadə olunur.
        List<String> tags;
        if (req.getTags() != null) {
            tags = new ArrayList<>(req.getTags());
        } else {
            tags = new ArrayList<>();
        }
        problem.setTags(tags);
        problem.setStatement(req.getStatement());
        problem.setInputSpec(req.getInputSpec());
        problem.setOutputSpec(req.getOutputSpec());
        // exampleInput göndərilməyibsə (null), boş sətir istifadə olunur.
        String exampleInput;
        if (req.getExampleInput() != null) {
            exampleInput = req.getExampleInput();
        } else {
            exampleInput = "";
        }
        problem.setExampleInput(exampleInput);
        problem.setExampleOutput(req.getExampleOutput());
        problem.setApproach(req.getApproach());
        problem.setReferenceSolution(req.getReferenceSolution());
        return toDto(problemRepository.save(problem));
    }

    // Mövcud problemi ID-yə görə tapıb bütün sahələrini yeni dəyərlərlə əvəz edir.
    @Transactional
    public AdminProblemDto updateProblem(Long id, ProblemUpsertRequest req) {
        Problem problem = problemRepository.findById(id).orElseThrow(() -> new NotFoundException("Məsələ tapılmadı: " + id));
        Topic topic = topicRepository.findBySlug(req.getTopicSlug());
        if (topic == null) {
            throw new NotFoundException("Mövzu tapılmadı: " + req.getTopicSlug());
        }
        // Problemin mövzusu da dəyişdirilə bilər (məs. yanlış mövzuya
        // düşmüş problemi düzgün mövzuya köçürmək üçün).
        problem.setTopic(topic);
        // orderIndex göndərilməyibsə (null), default olaraq 0 istifadə olunur.
        int orderIndex;
        if (req.getOrderIndex() != null) {
            orderIndex = req.getOrderIndex();
        } else {
            orderIndex = 0;
        }
        problem.setOrderIndex(orderIndex);
        problem.setSubgroupLabel(req.getSubgroupLabel());
        problem.setTitle(req.getTitle());
        problem.setDifficulty(req.getDifficulty());
        // tags göndərilməyibsə (null), boş siyahı istifadə olunur.
        List<String> tags;
        if (req.getTags() != null) {
            tags = new ArrayList<>(req.getTags());
        } else {
            tags = new ArrayList<>();
        }
        problem.setTags(tags);
        problem.setStatement(req.getStatement());
        problem.setInputSpec(req.getInputSpec());
        problem.setOutputSpec(req.getOutputSpec());
        // exampleInput göndərilməyibsə (null), boş sətir istifadə olunur.
        String exampleInput;
        if (req.getExampleInput() != null) {
            exampleInput = req.getExampleInput();
        } else {
            exampleInput = "";
        }
        problem.setExampleInput(exampleInput);
        problem.setExampleOutput(req.getExampleOutput());
        problem.setApproach(req.getApproach());
        problem.setReferenceSolution(req.getReferenceSolution());
        return toDto(problemRepository.save(problem));
    }

    // Problemi ID-yə görə silir.
    @Transactional
    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new NotFoundException("Məsələ tapılmadı: " + id);
        }
        problemRepository.deleteById(id);
    }

    // Entity -> DTO çevirmə köməkçiləri. Java-nın overload dəstəyi
    // sayəsində eyni "toDto" adı Problem və Topic üçün ayrıca istifadə
    // oluna bilir (parametr tipinə görə fərqləndirilir).
    private AdminTopicDto toDto(Topic t) {
        return new AdminTopicDto(t.getId(), t.getSlug(), t.getOrderIndex(), t.getTitle(), t.getMonthTag(), t.getDescription(), t.isPublished());
    }

    private AdminProblemDto toDto(Problem p) {
        return new AdminProblemDto(
            p.getId(), p.getTopic().getSlug(), p.getOrderIndex(), p.getSubgroupLabel(), p.getTitle(),
            p.getDifficulty(), p.getTags(), p.getStatement(), p.getInputSpec(), p.getOutputSpec(),
            p.getExampleInput(), p.getExampleOutput(), p.getApproach(), p.getReferenceSolution()
        );
    }

    // Admin panelindəki şagird siyahısı — hər şagirdin cəmi neçə fərqli
    // problem həll etdiyi də (totalSolved) birlikdə hesablanır.
    @Transactional(readOnly = true)
    public List<AdminUserDto> listStudents() {
        List<User> students = userRepository.findAllByRole(Role.STUDENT);
        // Hər şagird üçün, onun cəmi neçə fərqli problem həll etdiyini
        // (submissionRepository ilə) hesablayıb AdminUserDto-ya çevirir.
        List<AdminUserDto> result = new ArrayList<>();
        for (User u : students) {
            result.add(new AdminUserDto(u.getId(), u.getFullName(), u.getEmail(), u.getCreatedAt(), submissionRepository.countDistinctSolvedProblems(u)));
        }
        return result;
    }

    // Hələ təsdiqlənməmiş müəllim hesabları — admin panelinin "Müəllim
    // təsdiqləri" ekranı bunu göstərir.
    @Transactional(readOnly = true)
    public List<AdminUserDto> listPendingTeachers() {
        List<User> teachers = userRepository.findAllByRole(Role.TEACHER);
        // Bütün TEACHER-lər arasından yalnız hələ təsdiqlənməmişləri seçir.
        List<AdminUserDto> result = new ArrayList<>();
        for (User u : teachers) {
            if (!u.isApproved()) {
                result.add(new AdminUserDto(u.getId(), u.getFullName(), u.getEmail(), u.getCreatedAt(), 0L));
            }
        }
        return result;
    }

    // Müəllim hesabını təsdiqləyir (approved=true edir) — yalnız TEACHER rolunda olanlar üçün keçərlidir.
    @Transactional
    public void approveTeacher(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Müəllim tapılmadı: " + id));
        if (user.getRole() != Role.TEACHER) {
            throw new ConflictException("Bu istifadəçi müəllim deyil");
        }
        user.setApproved(true);
        userRepository.save(user);
    }

    // "Rədd et" — hesabı sadəcə deaktiv etmək əvəzinə TAMAMILƏ SİLİR, çünki
    // hələ təsdiqlənməmiş, real istifadə olunmayan bir qeydiyyat cəhdidir.
    @Transactional
    public void rejectTeacher(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Müəllim tapılmadı: " + id));
        if (user.getRole() != Role.TEACHER) {
            throw new ConflictException("Bu istifadəçi müəllim deyil");
        }
        userRepository.delete(user);
    }

    // Bazadakı bütün istifadəçilərin (rolundan asılı olmayaraq) tam detallı siyahısını qaytarır.
    @Transactional(readOnly = true)
    public List<AdminUserDetailDto> listAllUsers() {
        List<User> users = userRepository.findAll();
        // Hər User entity-sini AdminUserDetailDto formasına çevirir.
        List<AdminUserDetailDto> result = new ArrayList<>();
        for (User u : users) {
            result.add(toDetailDto(u));
        }
        return result;
    }

    // Tək bir istifadəçinin tam detallarını ID-yə görə qaytarır (tapılmasa NotFoundException atır).
    @Transactional(readOnly = true)
    public AdminUserDetailDto getUser(Long id) {
        return toDetailDto(findUserOrThrow(id));
    }

    @Transactional
    public AdminUserDetailDto updateProfile(Long id, UserProfileUpdateRequest req) {
        User user = findUserOrThrow(id);
        // E-poçt YALNIZ dəyişəndə unikallıq yoxlanılır (ignoreCase ilə) —
        // əks halda istifadəçi öz mövcud e-poçtunu "təkrar göndərəndə" belə
        // "artıq istifadə olunur" xətası alardı.
        if (!user.getEmail().equalsIgnoreCase(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Bu email artıq başqa istifadəçi tərəfindən istifadə olunur");
        }
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        return toDetailDto(userRepository.save(user));
    }

    // Bir istifadəçinin rolunu dəyişir. currentAdminId — bunu çağıran adminin
    // ID-sidir (özünü redaktə etməsinin qarşısını almaq üçün).
    @Transactional
    public AdminUserDetailDto changeRole(Long id, Role newRole, Long currentAdminId) {
        User user = findUserOrThrow(id);
        // Admin öz rolunu dəyişə bilməz — əks halda diqqətsizlikdən özünü
        // sistemdən "kilidləyə" bilərdi (heç kimin admin qalmadığı vəziyyət).
        if (user.getId().equals(currentAdminId)) {
            throw new ConflictException("Öz rolunu dəyişə bilməzsən");
        }
        // Sistemdə YALNIZ 1 admin qalıbsa, onun rolunu dəyişmək qadağandır —
        // bu, "heç bir admin qalmayan" bərpa olunmaz vəziyyətin qarşısını alır.
        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new ConflictException("Sistemdəki son admin hesabının rolu dəyişdirilə bilməz");
        }
        user.setRole(newRole);
        // TEACHER-dən başqa hər rola keçidlə birlikdə avtomatik təsdiqlənir
        // (STUDENT/ADMIN-in "approved" statusu mənasızdır, yalnız TEACHER üçün əhəmiyyətlidir).
        if (newRole != Role.TEACHER) {
            user.setApproved(true);
        }
        return toDetailDto(userRepository.save(user));
    }

    // Bir istifadəçini aktiv/deaktiv edir — silmək əvəzinə, tarixçəni saxlayaraq
    // girişini bloklamaq üçün istifadə olunur.
    @Transactional
    public AdminUserDetailDto setActive(Long id, boolean active, Long currentAdminId) {
        User user = findUserOrThrow(id);
        // Admin özünü deaktiv edərək özünü sistemdən çıxara bilməz.
        if (user.getId().equals(currentAdminId) && !active) {
            throw new ConflictException("Öz hesabını deaktiv edə bilməzsən");
        }
        // Son (yeganə) admini deaktiv etmək də eyni səbəbdən qadağandır
        // (bax: changeRole-dakı oxşar qoruma).
        if (!active && user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new ConflictException("Sistemdəki son admin hesabı deaktiv edilə bilməz");
        }
        user.setActive(active);
        return toDetailDto(userRepository.save(user));
    }

    // Admin bir istifadəçinin parolunu sıfırlayır — ya öz istədiyi yeni
    // parolu təyin edə bilər, ya da boş buraxaraq təsadüfi güclü parol
    // yaratdıra bilər (bax: generateRandomPassword).
    @Transactional
    public String resetPassword(Long id, String requestedPassword) {
        User user = findUserOrThrow(id);
        String newPassword;
        if (requestedPassword == null || requestedPassword.isBlank()) {
            newPassword = generateRandomPassword();
        } else {
            newPassword = requestedPassword;
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Açıq mətn parol YALNIZ bir dəfəlik, bu cavabda qaytarılır —
        // bazada heç vaxt saxlanılmır (yalnız hash-i saxlanılır).
        return newPassword;
    }

    @Transactional
    public void deleteUser(Long id, Long currentAdminId) {
        User user = findUserOrThrow(id);
        // Admin özünü silə bilməz (yenə eyni "sistemi kilidləməmə" məntiqi).
        if (user.getId().equals(currentAdminId)) {
            throw new ConflictException("Öz hesabını silə bilməzsən");
        }
        if (user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new ConflictException("Sistemdəki son admin hesabı silinə bilməz");
        }
        userRepository.delete(user);
    }

    // ID ilə istifadəçini tapan, tapılmasa NotFoundException atan köməkçi metod
    // (bu sinifdəki demək olar hər metodda təkrarlanan naxışı mərkəzləşdirir).
    private User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("İstifadəçi tapılmadı: " + id));
    }

    // Kriptoqrafik cəhətdən təhlükəsiz təsadüfi parol yaradır. Qarışdırıcı
    // ola bilən simvollar (böyük I/O, kiçik l, rəqəm 0/1) PASSWORD_CHARS
    // siyahısından bilərəkdən çıxarılıb ki, admin parolu əl ilə köçürəndə səhv etməsin.
    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(GENERATED_PASSWORD_LENGTH);
        // Hər addımda PASSWORD_CHARS-dan təsadüfi bir simvol seçib əlavə edir,
        // beləliklə GENERATED_PASSWORD_LENGTH uzunluğunda təsadüfi parol qurulur.
        for (int i = 0; i < GENERATED_PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(secureRandom.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    // Yalnız STUDENT rolunda "həll edilmiş problem sayı" mənalıdır —
    // TEACHER/ADMIN üçün bu sahə həmişə 0 qaytarılır (əlavə, faydasız
    // sorğu aparmamaq üçün).
    private AdminUserDetailDto toDetailDto(User u) {
        long solved;
        if (u.getRole() == Role.STUDENT) {
            solved = submissionRepository.countDistinctSolvedProblems(u);
        } else {
            solved = 0L;
        }
        return new AdminUserDetailDto(
            u.getId(), u.getFullName(), u.getEmail(), u.getRole(),
            u.isApproved(), u.isActive(), u.getCreatedAt(), solved
        );
    }
}

package az.azcup.backend.quiz;

import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.GroupMember;
import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.QuizAttempt;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.repository.GroupMemberRepository;
import az.azcup.backend.repository.GroupRepository;
import az.azcup.backend.repository.QuizAttemptRepository;
import az.azcup.backend.repository.QuizRepository;
import az.azcup.backend.service.QuizAdminService;
import az.azcup.backend.repository.UserRepository;
import az.azcup.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// İnformatika testləri modulunun uçdan-uca testi (MockMvc + H2): müəllim test yaradır, şagird həll edir; cavab açarının sızmaması,
// sahiblik, kilid qaydaları, qrup əhatəli nəticələr və vaxt məhdudiyyəti yoxlanılır.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private QuizAttemptRepository attemptRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizAdminService quizAdminService;

    // ---- yardımçılar ----

    private User createUser(Role role) {
        User user = new User();
        user.setFullName(role.name() + " Test");
        user.setEmail(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setApproved(true);
        user.setActive(true);
        return userRepository.save(user);
    }

    private String auth(User user) {
        return "Bearer " + jwtService.generateToken(user);
    }

    private static Map<String, Object> choice(String prompt, List<String> options, int correctIndex, String explanation) {
        Map<String, Object> q = new LinkedHashMap<>();
        q.put("type", "CHOICE");
        q.put("prompt", prompt);
        q.put("options", options);
        q.put("correctIndex", correctIndex);
        q.put("explanation", explanation);
        return q;
    }

    private static Map<String, Object> numeric(String prompt, String correctText) {
        Map<String, Object> q = new LinkedHashMap<>();
        q.put("type", "NUMERIC");
        q.put("prompt", prompt);
        q.put("code", "x = 5\nprint(x * 2)");
        q.put("correctText", correctText);
        return q;
    }

    private static Map<String, Object> quizBody(String title, boolean published, Integer timeLimit, List<Map<String, Object>> questions) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("description", "Sınaq testi");
        body.put("category", "Python");
        body.put("timeLimitMinutes", timeLimit);
        body.put("published", published);
        body.put("questions", questions);
        return body;
    }

    private List<Map<String, Object>> threeQuestions() {
        return List.of(
            choice("Hansı doğrudur?", List.of("A", "B", "C"), 1, "B düzgündür"),
            numeric("Çıxış nədir?", "10|10.0"),
            choice("İkinci sual", List.of("X", "Y"), 0, null)
        );
    }

    private JsonNode json(ResultActions result) throws Exception {
        return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
    }

    private JsonNode createQuiz(User teacher, Map<String, Object> body) throws Exception {
        return json(mockMvc.perform(post("/api/teacher/quizzes")
                .header("Authorization", auth(teacher)).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated()));
    }

    private JsonNode startAttempt(User student, long quizId) throws Exception {
        return json(mockMvc.perform(post("/api/quizzes/" + quizId + "/attempts").header("Authorization", auth(student)))
            .andExpect(status().isOk()));
    }

    private ResultActions submit(User student, long attemptId, Map<Long, String> answers) throws Exception {
        List<Map<String, Object>> items = new ArrayList<>();
        answers.forEach((qid, a) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionId", qid);
            item.put("answer", a);
            items.add(item);
        });
        return mockMvc.perform(post("/api/quiz-attempts/" + attemptId + "/submit")
            .header("Authorization", auth(student)).contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of("answers", items))));
    }

    private static long qid(JsonNode quiz, int index) {
        return quiz.get("questions").get(index).get("id").asLong();
    }

    // ---- testlər ----

    @Test
    void teacherBuildsQuizAndStudentSolvesItWithoutSeeingTheAnswerKeyBeforehand() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        JsonNode quiz = createQuiz(teacher, quizBody("Python testi", true, null, threeQuestions()));
        long quizId = quiz.get("id").asLong();
        assertThat(quiz.get("questions").size()).isEqualTo(3);

        mockMvc.perform(get("/api/quizzes").header("Authorization", auth(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")].questionCount").value(3))
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")].inProgress").value(false));

        String startBody = mockMvc.perform(post("/api/quizzes/" + quizId + "/attempts").header("Authorization", auth(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.questions.length()").value(3))
            .andExpect(jsonPath("$.deadlineAt").doesNotExist())
            .andReturn().getResponse().getContentAsString();
        // Həll zamanı cavab açarı və izah heç bir şəkildə şagirdə çatmamalıdır.
        assertThat(startBody).doesNotContain("correctIndex", "correctText", "explanation", "B düzgündür");
        JsonNode start = objectMapper.readTree(startBody);
        long attemptId = start.get("attemptId").asLong();
        long q1 = start.get("questions").get(0).get("id").asLong();
        long q2 = start.get("questions").get(1).get("id").asLong();
        long q3 = start.get("questions").get(2).get("id").asLong();

        // 1-ci düzgün, 2-ci boşluq+vergüllə yazılmış düzgün ədəd, 3-cü yanlış
        submit(student, attemptId, Map.of(q1, "1", q2, " 10,0 ", q3, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correctCount").value(2))
            .andExpect(jsonPath("$.totalCount").value(3))
            .andExpect(jsonPath("$.percent").value(67))
            .andExpect(jsonPath("$.late").value(false))
            .andExpect(jsonPath("$.items[0].correct").value(true))
            .andExpect(jsonPath("$.items[0].explanation").value("B düzgündür"))
            .andExpect(jsonPath("$.items[1].correct").value(true))
            .andExpect(jsonPath("$.items[2].correct").value(false))
            .andExpect(jsonPath("$.items[2].correctIndex").value(0));

        // İkinci təhvil rədd edilir
        submit(student, attemptId, Map.of(q1, "1")).andExpect(status().isConflict());

        mockMvc.perform(get("/api/quiz-attempts/" + attemptId).header("Authorization", auth(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correctCount").value(2));

        mockMvc.perform(get("/api/quizzes").header("Authorization", auth(student)))
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")].bestPercent").value(67))
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")].attemptCount").value(1));
    }

    @Test
    void unansweredQuestionsCountAsWrongAndUnknownQuestionIdsAreIgnored() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        JsonNode quiz = createQuiz(teacher, quizBody("Boş cavablar", true, null, threeQuestions()));
        long quizId = quiz.get("id").asLong();
        long attemptId = startAttempt(student, quizId).get("attemptId").asLong();

        submit(student, attemptId, Map.of(999999L, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correctCount").value(0))
            .andExpect(jsonPath("$.totalCount").value(3))
            .andExpect(jsonPath("$.items[0].yourAnswer").doesNotExist());
    }

    @Test
    void unpublishedQuizIsHiddenFromStudentsButOwnerCanPreviewIt() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        long quizId = createQuiz(teacher, quizBody("Qaralama", false, null, threeQuestions())).get("id").asLong();

        mockMvc.perform(get("/api/quizzes").header("Authorization", auth(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")]").isEmpty());
        mockMvc.perform(post("/api/quizzes/" + quizId + "/attempts").header("Authorization", auth(student)))
            .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/quizzes/" + quizId + "/attempts").header("Authorization", auth(teacher)))
            .andExpect(status().isOk());
    }

    @Test
    void anotherStudentCannotReadOrSubmitSomeoneElsesAttempt() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User alice = createUser(Role.STUDENT);
        User bob = createUser(Role.STUDENT);
        long quizId = createQuiz(teacher, quizBody("Hesab", true, null, threeQuestions())).get("id").asLong();
        long attemptId = startAttempt(alice, quizId).get("attemptId").asLong();
        submit(alice, attemptId, Map.of()).andExpect(status().isOk());

        mockMvc.perform(get("/api/quiz-attempts/" + attemptId).header("Authorization", auth(bob)))
            .andExpect(status().isNotFound());
        long bobsAttempt = startAttempt(bob, quizId).get("attemptId").asLong();
        mockMvc.perform(get("/api/quiz-attempts/" + bobsAttempt).header("Authorization", auth(bob)))
            .andExpect(status().isConflict()); // öz cəhdi, amma hələ təhvil verilməyib
        submit(bob, attemptId, Map.of()).andExpect(status().isNotFound());
    }

    @Test
    void onlyTheOwnerOrAdminManagesAQuizAndStudentsCannotUseTheTeacherApi() throws Exception {
        User owner = createUser(Role.TEACHER);
        User other = createUser(Role.TEACHER);
        User admin = createUser(Role.ADMIN);
        User student = createUser(Role.STUDENT);
        long quizId = createQuiz(owner, quizBody("Sahiblik", true, null, threeQuestions())).get("id").asLong();

        mockMvc.perform(get("/api/teacher/quizzes").header("Authorization", auth(student))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/teacher/quizzes/" + quizId).header("Authorization", auth(other))).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/teacher/quizzes/" + quizId).header("Authorization", auth(other))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizBody("Oğurluq", true, null, threeQuestions()))))
            .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/teacher/quizzes/" + quizId).header("Authorization", auth(other))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/teacher/quizzes/" + quizId + "/results").header("Authorization", auth(other))).andExpect(status().isForbidden());

        mockMvc.perform(get("/api/teacher/quizzes/" + quizId).header("Authorization", auth(owner))).andExpect(status().isOk());
        mockMvc.perform(get("/api/teacher/quizzes/" + quizId).header("Authorization", auth(admin))).andExpect(status().isOk());

        // Müəllimin siyahısında yalnız öz testi, admin-də hamısı
        mockMvc.perform(get("/api/teacher/quizzes").header("Authorization", auth(other)))
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")]").isEmpty());
        mockMvc.perform(get("/api/teacher/quizzes").header("Authorization", auth(admin)))
            .andExpect(jsonPath("$[?(@.id==" + quizId + ")]").isNotEmpty());
    }

    @Test
    void solvedQuizIsLockedAgainstAnswerKeyChangesButStillAllowsTextFixesAndNewQuestions() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        JsonNode quiz = createQuiz(teacher, quizBody("Kilid", true, null, threeQuestions()));
        long quizId = quiz.get("id").asLong();
        long attemptId = startAttempt(student, quizId).get("attemptId").asLong();
        submit(student, attemptId, Map.of()).andExpect(status().isOk());

        // Mövcud sualları id-ləri ilə geri göndərmək üçün redaktor görünüşünü oxuyuruq
        JsonNode detail = json(mockMvc.perform(get("/api/teacher/quizzes/" + quizId).header("Authorization", auth(teacher)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.locked").value(true)));
        List<Map<String, Object>> existing = new ArrayList<>();
        for (JsonNode q : detail.get("questions")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", q.get("id").asLong());
            m.put("type", q.get("type").asString());
            m.put("prompt", q.get("prompt").asString());
            if (!q.get("code").isNull()) {
                m.put("code", q.get("code").asString());
            }
            List<String> options = new ArrayList<>();
            q.get("options").forEach(o -> options.add(o.asString()));
            m.put("options", options);
            m.put("correctIndex", q.get("correctIndex").isNull() ? null : q.get("correctIndex").asInt());
            m.put("correctText", q.get("correctText").isNull() ? null : q.get("correctText").asString());
            existing.add(m);
        }

        // 1) düzgün variantı dəyişmək → 409
        List<Map<String, Object>> changedKey = deepCopy(existing);
        changedKey.get(0).put("correctIndex", 2);
        putQuiz(teacher, quizId, quizBody("Kilid", true, null, changedKey)).andExpect(status().isConflict());

        // 2) sual silmək → 409
        putQuiz(teacher, quizId, quizBody("Kilid", true, null, deepCopy(existing).subList(0, 2))).andExpect(status().isConflict());

        // 3) mətn düzəlişi + yeni sual əlavəsi → 200
        List<Map<String, Object>> fixed = deepCopy(existing);
        fixed.get(0).put("prompt", "Hansı ifadə doğrudur?");
        fixed.add(choice("Yeni sual", List.of("1", "2"), 1, null));
        putQuiz(teacher, quizId, quizBody("Kilid (düzəliş)", true, null, fixed))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Kilid (düzəliş)"))
            .andExpect(jsonPath("$.questions.length()").value(4))
            .andExpect(jsonPath("$.questions[0].prompt").value("Hansı ifadə doğrudur?"))
            .andExpect(jsonPath("$.questions[3].id").isNumber());

        // 4) həll olunmuş testi silmək olmaz, amma dərcdən çıxarmaq olar
        mockMvc.perform(delete("/api/teacher/quizzes/" + quizId).header("Authorization", auth(teacher)))
            .andExpect(status().isConflict());
        List<Map<String, Object>> after = deepCopy(fixed);
        after.get(3).put("id", json(mockMvc.perform(get("/api/teacher/quizzes/" + quizId).header("Authorization", auth(teacher))))
            .get("questions").get(3).get("id").asLong());
        putQuiz(teacher, quizId, quizBody("Kilid (düzəliş)", false, null, after))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void unsolvedQuizCanBeEditedFreelyAndDeleted() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        long quizId = createQuiz(teacher, quizBody("Sərbəst", true, null, threeQuestions())).get("id").asLong();
        startAttempt(student, quizId); // davam edən, təhvil verilməmiş cəhd silməyə mane olmur

        putQuiz(teacher, quizId, quizBody("Sərbəst", true, null, List.of(choice("Tək sual", List.of("a", "b"), 1, null))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.questions.length()").value(1))
            .andExpect(jsonPath("$.locked").value(false));

        mockMvc.perform(delete("/api/teacher/quizzes/" + quizId).header("Authorization", auth(teacher)))
            .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/teacher/quizzes/" + quizId).header("Authorization", auth(teacher)))
            .andExpect(status().isNotFound());
    }

    @Test
    void resultsAreLimitedToTheTeachersOwnGroupsAndAdminSeesEveryone() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User inGroup = createUser(Role.STUDENT);
        User outsider = createUser(Role.STUDENT);
        Group group = new Group();
        group.setName("10-A");
        group.setTeacher(teacher);
        group = groupRepository.save(group);
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setStudent(inGroup);
        member.setJoinedAt(Instant.now());
        groupMemberRepository.save(member);

        JsonNode quiz = createQuiz(teacher, quizBody("Nəticələr", true, null, threeQuestions()));
        long quizId = quiz.get("id").asLong();
        for (User s : List.of(inGroup, outsider)) {
            JsonNode start = startAttempt(s, quizId);
            long a = start.get("attemptId").asLong();
            // inGroup 1-ci sualı düz cavablayır, outsider heç nə
            Map<Long, String> answers = s == inGroup ? Map.of(start.get("questions").get(0).get("id").asLong(), "1") : Map.of();
            submit(s, a, answers).andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/teacher/quizzes/" + quizId + "/results").header("Authorization", auth(teacher)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scope").value("MY_GROUPS"))
            .andExpect(jsonPath("$.students.length()").value(1))
            .andExpect(jsonPath("$.students[0].email").value(inGroup.getEmail()))
            .andExpect(jsonPath("$.students[0].bestCorrect").value(1))
            .andExpect(jsonPath("$.questions.length()").value(3))
            .andExpect(jsonPath("$.questions[0].correct").value(1))
            .andExpect(jsonPath("$.questions[0].percent").value(100));

        mockMvc.perform(get("/api/teacher/quizzes/" + quizId + "/results").header("Authorization", auth(createUser(Role.ADMIN))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scope").value("ALL"))
            .andExpect(jsonPath("$.students.length()").value(2));
    }

    @Test
    void brokenQuestionsAreRejectedWithClearMessages() throws Exception {
        User teacher = createUser(Role.TEACHER);
        Map<String, Object> oneOption = choice("Bir variant", List.of("yalnız"), 0, null);
        Map<String, Object> badIndex = choice("Səhv indeks", List.of("a", "b"), 5, null);
        Map<String, Object> blankOption = choice("Boş variant", List.of("a", " "), 0, null);
        Map<String, Object> noAnswer = numeric("Cavabsız", "  ");
        Map<String, Object> emptyAlt = numeric("Boş seçim", "5||6");
        Map<String, Object> weirdType = numeric("Naməlum", "1");
        weirdType.put("type", "ESSAY");

        for (Map<String, Object> bad : List.of(oneOption, badIndex, blankOption, noAnswer, emptyAlt, weirdType)) {
            mockMvc.perform(post("/api/teacher/quizzes").header("Authorization", auth(teacher)).contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(quizBody("Səhv", false, null, List.of(bad)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("Sual 1:")));
        }
        // Sualsız test dərc edilə bilməz
        mockMvc.perform(post("/api/teacher/quizzes").header("Authorization", auth(teacher)).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizBody("Boş", true, null, List.of()))))
            .andExpect(status().isBadRequest());
        // Çox böyük vaxt məhdudiyyəti və boş ad
        mockMvc.perform(post("/api/teacher/quizzes").header("Authorization", auth(teacher)).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizBody("Vaxt", false, 5000, threeQuestions()))))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/teacher/quizzes").header("Authorization", auth(teacher)).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizBody("   ", false, null, threeQuestions()))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void timeLimitIsEnforcedByTheServerClock() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        long quizId = createQuiz(teacher, quizBody("Vaxtlı", true, 5, threeQuestions())).get("id").asLong();

        JsonNode start = startAttempt(student, quizId);
        long attemptId = start.get("attemptId").asLong();
        assertThat(start.get("deadlineAt").isNull()).isFalse();
        assertThat(start.get("serverNow").isNull()).isFalse();

        // Eyni davam edən cəhd qaytarılır (yeni cəhd yaranmır)
        assertThat(startAttempt(student, quizId).get("attemptId").asLong()).isEqualTo(attemptId);

        // Cəhdi 10 dəqiqə əvvələ çəkirik: 5 dəqiqəlik limit + 60 san. güzəşt keçib → "gec"
        QuizAttempt attempt = attemptRepository.findById(attemptId).orElseThrow();
        attempt.setStartedAt(Instant.now().minus(10, ChronoUnit.MINUTES));
        attemptRepository.save(attempt);
        submit(student, attemptId, Map.of()).andExpect(status().isOk()).andExpect(jsonPath("$.late").value(true));

        // Vaxtı keçmiş davam edən cəhd yenidən başlayanda sayğac sıfırlanır
        long second = startAttempt(student, quizId).get("attemptId").asLong();
        QuizAttempt stale = attemptRepository.findById(second).orElseThrow();
        stale.setStartedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        attemptRepository.save(stale);
        JsonNode restarted = startAttempt(student, quizId);
        assertThat(restarted.get("attemptId").asLong()).isEqualTo(second);
        assertThat(Instant.parse(restarted.get("startedAt").asString())).isAfter(Instant.now().minus(1, ChronoUnit.MINUTES));
    }

    @Test
    void cleanupRemovesOnlyTheOldReadyMadeQuizzesTogetherWithTheirAttempts() throws Exception {
        User teacher = createUser(Role.TEACHER);
        User student = createUser(Role.STUDENT);
        long seededId = createQuiz(teacher, quizBody("Köhnə hazır test", true, null, threeQuestions())).get("id").asLong();
        long handMadeId = createQuiz(teacher, quizBody("Əl ilə yaradılan test", true, null, threeQuestions())).get("id").asLong();

        // Hər iki testi şagird həll edir (cəhd + cavab sətirləri yaranır), hazır testdə əlavə olaraq davam edən cəhd də qalır
        long seededAttempt = startAttempt(student, seededId).get("attemptId").asLong();
        submit(student, seededAttempt, Map.of()).andExpect(status().isOk());
        startAttempt(student, seededId);
        long handMadeAttempt = startAttempt(student, handMadeId).get("attemptId").asLong();
        submit(student, handMadeAttempt, Map.of()).andExpect(status().isOk());

        // Köhnə buraxılışın yükləyicisi kimi: testə seedKey veririk
        Quiz seeded = quizRepository.findById(seededId).orElseThrow();
        seeded.setSeedKey("dim-python-test-" + UUID.randomUUID());
        quizRepository.save(seeded);

        assertThat(quizAdminService.deleteSeededQuizzes()).isEqualTo(1);
        // Təkrar çağırış heç nə etmir
        assertThat(quizAdminService.deleteSeededQuizzes()).isZero();

        // Hazır test, onun cəhdi və nəticəsi yoxdur
        assertThat(quizRepository.existsById(seededId)).isFalse();
        assertThat(attemptRepository.existsById(seededAttempt)).isFalse();
        mockMvc.perform(get("/api/quiz-attempts/" + seededAttempt).header("Authorization", auth(student))).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/quizzes").header("Authorization", auth(student)))
            .andExpect(jsonPath("$[?(@.id==" + seededId + ")]").isEmpty())
            .andExpect(jsonPath("$[?(@.id==" + handMadeId + ")]").isNotEmpty());

        // Əl ilə yaradılan test və onun nəticəsi yerindədir
        mockMvc.perform(get("/api/teacher/quizzes/" + handMadeId).header("Authorization", auth(teacher)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.questions.length()").value(3));
        mockMvc.perform(get("/api/quiz-attempts/" + handMadeAttempt).header("Authorization", auth(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount").value(3));
    }

    // ---- kiçik köməkçilər ----

    private ResultActions putQuiz(User teacher, long quizId, Map<String, Object> body) throws Exception {
        return mockMvc.perform(put("/api/teacher/quizzes/" + quizId)
            .header("Authorization", auth(teacher)).contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
    }

    private List<Map<String, Object>> deepCopy(List<Map<String, Object>> source) {
        List<Map<String, Object>> copy = new ArrayList<>();
        for (Map<String, Object> m : source) {
            copy.add(new LinkedHashMap<>(m));
        }
        return copy;
    }
}

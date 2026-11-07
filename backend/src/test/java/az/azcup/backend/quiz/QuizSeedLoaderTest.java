package az.azcup.backend.quiz;

import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.repository.QuizRepository;
import az.azcup.backend.repository.UserRepository;
import az.azcup.backend.security.JwtService;
import az.azcup.backend.seed.QuizSeedLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Layihə ilə gələn hazır testlərin (resources/seed/dim-python-tests.json) bazaya yazılması: yükləyici bu testdə AÇIQ şəkildə yandırılır,
// ayrıca H2 bazası istifadə olunur ki, digər inteqrasiya testləri ilə qarışmasın.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {
    "app.seed.quizzes.enabled=true",
    "spring.datasource.url=jdbc:h2:mem:azcup_seed_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizSeedLoaderTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private QuizSeedLoader quizSeedLoader;

    private JsonNode seedFile() throws Exception {
        try (InputStream in = new ClassPathResource("seed/dim-python-tests.json").getInputStream()) {
            return objectMapper.readTree(in);
        }
    }

    private User createUser(Role role) {
        User user = new User();
        user.setFullName(role.name() + " Seed");
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

    @Test
    void readyMadeTestsAreCreatedOncePublishedAndOwnedByTheAdmin() throws Exception {
        JsonNode file = seedFile();
        assertThat(file.size()).isEqualTo(11);

        // Başlanğıcda yükləyici artıq işləyib: hər fayl elementi üçün bir test, hamısı dərc olunmuş
        for (JsonNode item : file) {
            String key = item.get("seedKey").asString();
            assertThat(quizRepository.existsBySeedKey(key)).as(key).isTrue();
        }
        long before = quizRepository.count();

        // Təkrar işə salma heç nə yaratmır (idempotent)
        quizSeedLoader.run();
        quizSeedLoader.run();
        assertThat(quizRepository.count()).isEqualTo(before);

        // Şagird siyahısında sıra fayldakı kimidir: Python 1, 2, ... 10, sonda qarışıq sınaq
        User student = createUser(Role.STUDENT);
        JsonNode listed = objectMapper.readTree(mockMvc.perform(get("/api/quizzes").header("Authorization", auth(student)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(listed.size()).isEqualTo(11);
        for (int i = 0; i < 11; i++) {
            assertThat(listed.get(i).get("title").asString()).as("sıra " + i).isEqualTo(file.get(i).get("title").asString());
        }

        // Müəllim siyahıda hazır testləri görür, amma onları redaktə edə bilmir; admin sahibidir
        User teacher = createUser(Role.TEACHER);
        mockMvc.perform(get("/api/teacher/quizzes").header("Authorization", auth(teacher)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(11))
            .andExpect(jsonPath("$[0].seeded").value(true))
            .andExpect(jsonPath("$[0].canEdit").value(false));
    }

    @Test
    void studentsSolveEveryReadyMadeTestWithTheShippedAnswerKeyAndGetFullMarks() throws Exception {
        JsonNode file = seedFile();
        User student = createUser(Role.STUDENT);
        int solved = 0;
        for (JsonNode item : file) {
            Quiz quiz = quizRepository.findAll().stream()
                .filter(q -> item.get("seedKey").asString().equals(q.getSeedKey())).findFirst().orElseThrow();
            assertThat(quiz.isPublished()).isTrue();

            JsonNode start = objectMapper.readTree(mockMvc.perform(post("/api/quizzes/" + quiz.getId() + "/attempts").header("Authorization", auth(student)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            JsonNode questions = start.get("questions");
            assertThat(questions.size()).isEqualTo(item.get("questions").size());
            // Həll zamanı cavab açarı görünməməlidir
            assertThat(start.toString()).doesNotContain("correctIndex", "correctText", "explanation");

            List<Map<String, Object>> answers = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                JsonNode src = item.get("questions").get(i);
                Map<String, Object> a = new LinkedHashMap<>();
                a.put("questionId", questions.get(i).get("id").asLong());
                a.put("answer", "CHOICE".equals(src.get("type").asString())
                    ? String.valueOf(src.get("correctIndex").asInt())
                    : src.get("correctText").asString().split("\\|")[0]);
                answers.add(a);
            }
            mockMvc.perform(post("/api/quiz-attempts/" + start.get("attemptId").asLong() + "/submit")
                    .header("Authorization", auth(student)).contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("answers", answers))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percent").value(100))
                .andExpect(jsonPath("$.correctCount").value(questions.size()));
            solved++;
        }
        assertThat(solved).isEqualTo(11);
    }

    @Test
    void readyMadeTestsCannotBeDeletedButCanBeUnpublishedByTheAdmin() throws Exception {
        User admin = createUser(Role.ADMIN);
        Quiz quiz = quizRepository.findAll().stream().filter(q -> q.getSeedKey() != null).findFirst().orElseThrow();

        // Silinsə, yükləyici onu növbəti başlanğıcda yenidən yaradardı
        mockMvc.perform(delete("/api/teacher/quizzes/" + quiz.getId()).header("Authorization", auth(admin)))
            .andExpect(status().isConflict());

        // Admin redaktə edib dərcdən çıxara bilər; müəllim isə dəyişə bilməz (403)
        JsonNode detail = objectMapper.readTree(mockMvc.perform(get("/api/teacher/quizzes/" + quiz.getId()).header("Authorization", auth(admin)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(detail.get("published").asBoolean()).isTrue();
        User teacher = createUser(Role.TEACHER);
        mockMvc.perform(get("/api/teacher/quizzes/" + quiz.getId()).header("Authorization", auth(teacher))).andExpect(status().isForbidden());
        // Müəllim hazır testin nəticələrinə (öz qruplarının) baxa bilər
        mockMvc.perform(get("/api/teacher/quizzes/" + quiz.getId() + "/results").header("Authorization", auth(teacher)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scope").value("MY_GROUPS"));
    }
}

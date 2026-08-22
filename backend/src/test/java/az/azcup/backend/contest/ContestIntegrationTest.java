package az.azcup.backend.contest;

import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.repository.UserRepository;
import az.azcup.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Yarış (Contest) modulunun UCDAN-SONA inteqrasiya testi — AuthIntegrationTest-in
// eyni MockMvc+H2 üsulu ilə: real HTTP sorğuları simulyasiya olunur, real
// Spring konteksti və (test profili altında) real g++ kompilyatoru istifadə
// olunur (yalnız verilənlər bazası H2-dir). TEACHER/STUDENT istifadəçiləri
// açıq qeydiyyat axını (bu, TEACHER üçün admin təsdiqi tələb edir) əvəzinə
// birbaşa UserRepository ilə yaradılır — test məqsədilə daha sadə və sürətlidir.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContestIntegrationTest {

    // HTTP sorğularını real serverə ehtiyac olmadan simulyasiya etmək üçün.
    @Autowired
    private MockMvc mockMvc;

    // Test gövdələrini JSON-a çevirmək və JSON cavabları oxumaq üçün.
    @Autowired
    private ObjectMapper objectMapper;

    // Test istifadəçilərini birbaşa bazaya yazmaq üçün (register/approve
    // axınını keçmədən).
    @Autowired
    private UserRepository userRepository;

    // Test istifadəçilərinin parolunu hash-ləmək üçün (əslində bu testlərdə
    // parolla giriş edilmir, JWT birbaşa yaradılır — amma User.passwordHash
    // NOT NULL olduğu üçün formal bir dəyər lazımdır).
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Test istifadəçiləri üçün birbaşa (login axınını keçmədən) JWT yaratmaq üçün.
    @Autowired
    private JwtService jwtService;

    // Verilmiş rolla, TƏSADÜFİ email-li, təsdiqlənmiş+aktiv bir istifadəçi
    // yaradıb bazaya yazır və onun üçün "Bearer <token>" header dəyərini qaytarır.
    private String createUserAndGetAuthHeader(Role role) {
        User user = new User();
        user.setFullName(role.name() + " Test");
        user.setEmail(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setApproved(true);
        user.setActive(true);
        userRepository.save(user);
        return "Bearer " + jwtService.generateToken(user);
    }

    // TEACHER yarış+məsələ+test halı yaradır, STUDENT qoşulub düzgün/yanlış
    // kod göndərir, reytinq cədvəli düzgün balı göstərir — tam axının
    // "xoşbəxt yol" (happy path) ssenarisi.
    @Test
    void teacherCreatesContestAndStudentSolvesIt() throws Exception {
        String teacherAuth = createUserAndGetAuthHeader(Role.TEACHER);
        String studentAuth = createUserAndGetAuthHeader(Role.STUDENT);

        // ---- Addım 1: TEACHER yarış yaradır (indi başlayıb, 1 saatdan sonra bitir) ----
        Map<String, Object> contestBody = new HashMap<>();
        contestBody.put("title", "Sınaq Yarışı");
        contestBody.put("description", "İnteqrasiya testi üçün yarış");
        contestBody.put("startTime", Instant.now().minus(1, ChronoUnit.MINUTES).toString());
        contestBody.put("endTime", Instant.now().plus(1, ChronoUnit.HOURS).toString());

        String createContestResponse = mockMvc.perform(
                post("/api/teacher/contests")
                    .header("Authorization", teacherAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(contestBody))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andReturn().getResponse().getContentAsString();
        long contestId = objectMapper.readTree(createContestResponse).get("id").asLong();

        // ---- Addım 2: TEACHER məsələ əlavə edir (iki ədədin cəmi, 100 bal) ----
        Map<String, Object> problemBody = new HashMap<>();
        problemBody.put("orderIndex", 0);
        problemBody.put("title", "İki ədədin cəmi");
        problemBody.put("statement", "İki ədəd verilib, cəmini tapın.");
        problemBody.put("inputSpec", "İki tam ədəd");
        problemBody.put("outputSpec", "Cəmi");
        problemBody.put("points", 100);

        String addProblemResponse = mockMvc.perform(
                post("/api/teacher/contests/" + contestId + "/problems")
                    .header("Authorization", teacherAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(problemBody))
            )
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long problemId = objectMapper.readTree(addProblemResponse).get("id").asLong();

        // ---- Addım 3: TEACHER iki test halı əlavə edir (biri nümunə, biri gizli) ----
        addTestCase(teacherAuth, contestId, problemId, 0, "2 3", "5", false);
        addTestCase(teacherAuth, contestId, problemId, 1, "10 20", "30", true);

        // ---- Addım 4: STUDENT yarışın detallarına baxır — ACTIVE olduğu üçün məsələni görməlidir ----
        mockMvc.perform(get("/api/contests/" + contestId).header("Authorization", studentAuth))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.joined").value(false))
            .andExpect(jsonPath("$.problems.length()").value(1))
            // Gizli test halının (10 20 -> 30) girişi/çıxışı STUDENT-ə göstərilən
            // cavabda GÖRÜNMƏMƏLİDİR — yalnız nümunə (sample) test halı var.
            .andExpect(jsonPath("$.problems[0].sampleTestCases.length()").value(1))
            .andExpect(jsonPath("$.problems[0].sampleTestCases[0].input").value("2 3"));

        // ---- Addım 5: qoşulmadan göndərmə cəhdi rədd edilməlidir ----
        mockMvc.perform(
                post("/api/contests/problems/" + problemId + "/submissions")
                    .header("Authorization", studentAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sourceCodeBody(CORRECT_SUM_SOLUTION)))
            )
            .andExpect(status().isBadRequest());

        // ---- Addım 6: STUDENT yarışa qoşulur ----
        mockMvc.perform(post("/api/contests/" + contestId + "/join").header("Authorization", studentAuth))
            .andExpect(status().isNoContent());

        // Təkrar qoşulma cəhdi 409 CONFLICT qaytarmalıdır.
        mockMvc.perform(post("/api/contests/" + contestId + "/join").header("Authorization", studentAuth))
            .andExpect(status().isConflict());

        // ---- Addım 7: YANLIŞ kodla göndərmə — WRONG_ANSWER, 0 bal ----
        mockMvc.perform(
                post("/api/contests/problems/" + problemId + "/submissions")
                    .header("Authorization", studentAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sourceCodeBody(WRONG_SUM_SOLUTION)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("WRONG_ANSWER"))
            .andExpect(jsonPath("$.pointsAwarded").value(0))
            // Yanlış kod BİRİNCİ (nümunə) testdə uğursuz olmalıdır (0-cı sıra).
            .andExpect(jsonPath("$.firstFailedTestCaseOrder").value(0));

        // ---- Addım 8: DÜZGÜN kodla göndərmə — ACCEPTED, tam bal (100) ----
        String submitResponse = mockMvc.perform(
                post("/api/contests/problems/" + problemId + "/submissions")
                    .header("Authorization", studentAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sourceCodeBody(CORRECT_SUM_SOLUTION)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ACCEPTED"))
            .andExpect(jsonPath("$.passedTestCases").value(2))
            .andExpect(jsonPath("$.totalTestCases").value(2))
            .andExpect(jsonPath("$.pointsAwarded").value(100))
            .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(submitResponse).get("firstFailedTestCaseOrder").isNull()).isTrue();

        // ---- Addım 9: EYNİ düzgün kodu TƏKRAR göndərmək TƏKRAR bal VERMƏMƏLİDİR ----
        mockMvc.perform(
                post("/api/contests/problems/" + problemId + "/submissions")
                    .header("Authorization", studentAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sourceCodeBody(CORRECT_SUM_SOLUTION)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ACCEPTED"))
            .andExpect(jsonPath("$.pointsAwarded").value(0));

        // ---- Addım 10: reytinq cədvəlində 1 iştirakçı, 100 bal, 1-ci yer ----
        // (SecurityConfig-in "/api/** -> authenticated()" qaydası bu endpoint-i
        // də əhatə edir, ona görə giriş etmiş istənilən istifadəçi baxa bilər.)
        mockMvc.perform(get("/api/contests/" + contestId + "/leaderboard").header("Authorization", studentAuth))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].rank").value(1))
            .andExpect(jsonPath("$[0].totalPoints").value(100))
            .andExpect(jsonPath("$[0].solvedCount").value(1));

        // ---- Addım 11: tarixçədə DÖRD cəhd olmalıdır (yanlış, düzgün, düzgün-təkrar) ----
        mockMvc.perform(get("/api/contests/problems/" + problemId + "/submissions").header("Authorization", studentAuth))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    // Bitmiş (endTime keçmiş) bir yarışa kod göndərmə cəhdi, hətta düzgün
    // kod olsa belə, SERVER TƏRƏFİNDƏ rədd edilməlidir.
    @Test
    void submittingToEndedContestIsRejected() throws Exception {
        String teacherAuth = createUserAndGetAuthHeader(Role.TEACHER);
        String studentAuth = createUserAndGetAuthHeader(Role.STUDENT);

        Map<String, Object> contestBody = new HashMap<>();
        contestBody.put("title", "Bitmiş Yarış");
        contestBody.put("description", "Bitmiş yarışa göndərmə testi");
        contestBody.put("startTime", Instant.now().minus(2, ChronoUnit.HOURS).toString());
        contestBody.put("endTime", Instant.now().minus(1, ChronoUnit.HOURS).toString());

        String createContestResponse = mockMvc.perform(
                post("/api/teacher/contests")
                    .header("Authorization", teacherAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(contestBody))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ENDED"))
            .andReturn().getResponse().getContentAsString();
        long contestId = objectMapper.readTree(createContestResponse).get("id").asLong();

        Map<String, Object> problemBody = new HashMap<>();
        problemBody.put("orderIndex", 0);
        problemBody.put("title", "Sadə məsələ");
        problemBody.put("statement", "Cəm tap");
        problemBody.put("inputSpec", "İki ədəd");
        problemBody.put("outputSpec", "Cəm");
        problemBody.put("points", 50);
        String addProblemResponse = mockMvc.perform(
                post("/api/teacher/contests/" + contestId + "/problems")
                    .header("Authorization", teacherAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(problemBody))
            )
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long problemId = objectMapper.readTree(addProblemResponse).get("id").asLong();
        addTestCase(teacherAuth, contestId, problemId, 0, "1 1", "2", false);

        // Bitmiş yarışa qoşulmaq da mənasız olduğu üçün rədd edilir.
        mockMvc.perform(post("/api/contests/" + contestId + "/join").header("Authorization", studentAuth))
            .andExpect(status().isBadRequest());

        mockMvc.perform(
                post("/api/contests/problems/" + problemId + "/submissions")
                    .header("Authorization", studentAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sourceCodeBody(CORRECT_SUM_SOLUTION)))
            )
            .andExpect(status().isBadRequest());
    }

    // Doğru işləyən "iki ədədi oxu, cəmini çap et" C++ həlli.
    private static final String CORRECT_SUM_SOLUTION = """
        #include <bits/stdc++.h>
        using namespace std;
        int main(){ long long a,b; cin>>a>>b; cout<<a+b<<"\\n"; }
        """;

    // Bilərəkdən yanlış (toplamaq əvəzinə həmişə 0 çap edən) həll.
    private static final String WRONG_SUM_SOLUTION = """
        #include <bits/stdc++.h>
        using namespace std;
        int main(){ cout<<0<<"\\n"; }
        """;

    // ContestSubmissionRequest gövdəsini quran köməkçi metod.
    private Map<String, String> sourceCodeBody(String sourceCode) {
        Map<String, String> body = new HashMap<>();
        body.put("sourceCode", sourceCode);
        return body;
    }

    // Bir yarış məsələsinə test halı əlavə edən köməkçi metod (dublikasiyanı azaltmaq üçün).
    private void addTestCase(String teacherAuth, long contestId, long problemId, int orderIndex, String input, String expectedOutput, boolean hidden) throws Exception {
        Map<String, Object> testCaseBody = new HashMap<>();
        testCaseBody.put("orderIndex", orderIndex);
        testCaseBody.put("input", input);
        testCaseBody.put("expectedOutput", expectedOutput);
        testCaseBody.put("hidden", hidden);
        mockMvc.perform(
                post("/api/teacher/contests/" + contestId + "/problems/" + problemId + "/test-cases")
                    .header("Authorization", teacherAuth)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCaseBody))
            )
            .andExpect(status().isCreated());
    }
}

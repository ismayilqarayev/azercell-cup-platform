package az.azcup.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// /api/auth altındakı endpoint-lərin (register/login/me) UC-DAN-SONA
// (end-to-end) inteqrasiya testləri — real HTTP sorğuları MockMvc vasitəsilə
// simulyasiya olunur, real Spring konteksti və (test profili altında) real
// verilənlər bazası istifadə olunur.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    // HTTP sorğularını real serverə ehtiyac olmadan simulyasiya etmək üçün.
    @Autowired
    private MockMvc mockMvc;

    // Test gövdələrini JSON-a çevirmək və JSON cavabları oxumaq üçün.
    @Autowired
    private ObjectMapper objectMapper;

    // Uğurlu ssenari: qeydiyyatdan keç -> giriş et -> "mən kiməm" sorğusu ilə
    // token-in etibarlı olduğunu təsdiqlə. Hər dəfə TƏSADÜFİ e-poçt istifadə
    // olunur ki, testlər bir-birini təkrar işə salanda "email artıq mövcuddur"
    // xətası ilə toqquşmasınlar.
    @Test
    void registerThenLoginThenMe() throws Exception {
        String email = "student-" + UUID.randomUUID() + "@example.com";
        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("fullName", "Test Student");
        registerBody.put("email", email);
        registerBody.put("password", "password123");

        // Addım 1: qeydiyyat — 201 CREATED, doğru email/rol və boş olmayan token gözlənilir.
        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBody))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.role").value("STUDENT"))
            .andExpect(jsonPath("$.token").isNotEmpty());

        // Addım 2: eyni kredensiallarla giriş — 200 OK və yeni bir token gözlənilir.
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", "password123");
        String loginResponse = mockMvc.perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginBody))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        // Login cavabının JSON gövdəsindən token sətrini çıxarır.
        String token = objectMapper.readTree(loginResponse).get("token").asString();

        // Addım 3: alınan tokenlə "/me" sorğusu — token həqiqətən etibarlıdırsa,
        // server bizim öz email-imizi qaytarmalıdır.
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email));
    }

    // Mənfi ssenari: düzgün qeydiyyatdan sonra YANLIŞ parolla giriş cəhdi —
    // 401 UNAUTHORIZED qaytarılmalıdır (bax: GlobalExceptionHandler.handleBadCredentials).
    @Test
    void loginWithWrongPasswordIsUnauthorized() throws Exception {
        String email = "student-" + UUID.randomUUID() + "@example.com";
        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("fullName", "Test Student");
        registerBody.put("email", email);
        registerBody.put("password", "password123");
        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerBody))
            )
            .andExpect(status().isCreated());

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", "wrong-password");
        mockMvc.perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginBody))
            )
            .andExpect(status().isUnauthorized());
    }
}

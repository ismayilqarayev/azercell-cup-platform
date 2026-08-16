package az.azcup.backend.auth

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun registerThenLoginThenMe() {
        val email = "student-${UUID.randomUUID()}@example.com"
        val registerBody = mapOf(
            "fullName" to "Test Student",
            "email" to email,
            "password" to "password123"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerBody))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.role").value("STUDENT"))
            .andExpect(jsonPath("$.token").isNotEmpty())

        val loginBody = mapOf("email" to email, "password" to "password123")
        val loginResponse = mockMvc.perform(
            post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn().response.contentAsString

        val token = objectMapper.readTree(loginResponse).get("token").asString()

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
    }

    @Test
    fun loginWithWrongPasswordIsUnauthorized() {
        val email = "student-${UUID.randomUUID()}@example.com"
        val registerBody = mapOf(
            "fullName" to "Test Student",
            "email" to email,
            "password" to "password123"
        )
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerBody))
        )
            .andExpect(status().isCreated())

        val loginBody = mapOf("email" to email, "password" to "wrong-password")
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody))
        )
            .andExpect(status().isUnauthorized())
    }
}

package hey.jusang.invest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.controllers.SignController
import hey.jusang.invest.exceptions.ErrorCode
import hey.jusang.invest.exceptions.UserAlreadyExistedException
import hey.jusang.invest.exceptions.UserNotFoundException
import hey.jusang.invest.exceptions.WrongPasswordException
import hey.jusang.invest.models.InvestorDTO
import hey.jusang.invest.services.SignService
import hey.jusang.invest.utils.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SignController::class)
class SignControllerTests {
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var signService: SignService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider
    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())

    @Test
    fun `mock mvc should be configured`() {
    }

    @Test
    fun `we should sign in`() {
        whenever(signService.signIn("username", "password"))
            .thenReturn("TEST TOKEN")

        signIn("username", "password")
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("TEST TOKEN"))
    }

    @Test
    fun `we should sign up`() {
        val data = InvestorDTO(1, "username", "password")
        whenever(signService.signUp("username", "password"))
            .thenReturn(data)

        val resultActions = signUp("username", "password")
        resultActions.andExpect(status().isCreated)

        val content: String = resultActions.andReturn().response.contentAsString
        val investor: InvestorDTO = objectMapper.readValue(content)

        assert(investor == data)
    }

    @Test
    fun `we should handle UserNotFoundException while signing in with wrong name`() {
        whenever(signService.signIn("wrong name", "password"))
            .thenAnswer { throw UserNotFoundException() }

        signIn("wrong name", "password")
            .andExpect(status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.USER_NOT_FOUND))
    }

    @Test
    fun `we should handle WrongPasswordException while signing in with wrong password`() {
        whenever(signService.signIn("username", "wrong password"))
            .thenAnswer { throw WrongPasswordException() }

        signIn("username", "wrong password")
            .andExpect(status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.WRONG_PASSWORD))
    }

    @Test
    fun `we should handle UserAlreadyExistedException while signing up with existed name`() {
        whenever(signService.signUp("existed", "password"))
            .thenAnswer { throw UserAlreadyExistedException() }

        signUp("existed", "password")
            .andExpect(status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.USER_ALREADY_EXISTED))
    }

    private fun signIn(name: String, password: String): ResultActions {
        return mvc.perform(
            post("/signin")
                .param("name", name)
                .param("password", password)
        )
    }

    private fun signUp(name: String, password: String): ResultActions {
        return mvc.perform(
            post("/signup")
                .param("name", name)
                .param("password", password)
        )
    }
}
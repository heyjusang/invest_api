package hey.jusang.invest

import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.exceptions.UserAlreadyExistedException
import hey.jusang.invest.exceptions.UserNotFoundException
import hey.jusang.invest.exceptions.WrongPasswordException
import hey.jusang.invest.models.User
import hey.jusang.invest.repositories.SignRepository
import hey.jusang.invest.services.SignServiceImpl
import hey.jusang.invest.utils.JwtTokenProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
class SignServiceTests {
    @Mock
    lateinit var signRepository: SignRepository
    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider
    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    lateinit var signService: SignServiceImpl

    @Test
    fun `mock should be configured`() {
    }

    @Test
    fun `we should get token when signing in`() {
        val data = User(1, "username", "encodedPassword", "ROLE_USER", LocalDateTime.now())

        whenever(signRepository.selectUserByName("username"))
            .thenReturn(data)

        whenever(passwordEncoder.matches("password", "encodedPassword"))
            .thenReturn(true)

        whenever(jwtTokenProvider.createToken(data))
            .thenReturn("TEST TOKEN")

        val token: String = signService.signIn("username", "password")
        assert(token == "TEST TOKEN")
    }

    @Test
    fun `we should sign up`() {
        whenever(signRepository.selectUserCountByName("username"))
            .thenReturn(0)

        whenever(passwordEncoder.encode("password"))
            .thenReturn("encodedPassword")

        whenever(signRepository.insertUser("username", "encodedPassword"))
            .thenReturn(1)

        val success: Boolean = signService.signUp("username", "password")
        assert(success)
    }

    @Test
    fun `we should get UserNotFoundException when signing in with wrong name`() {
        whenever(signRepository.selectUserByName("wrong name"))
            .thenReturn(null)

        Assertions.assertThrows(UserNotFoundException::class.java) {
            signService.signIn("wrong name", "password")
        }
    }

    @Test
    fun `we should get WrongPasswordException when signing in with wrong password`() {
        val data = User(1, "username", "encodedPassword", "ROLE_USER", LocalDateTime.now())

        whenever(signRepository.selectUserByName("username"))
            .thenReturn(data)

        whenever(passwordEncoder.matches("wrong password", "encodedPassword"))
            .thenReturn(false)

        Assertions.assertThrows(WrongPasswordException::class.java) {
            signService.signIn("username", "wrong password")
        }
    }

    @Test
    fun `we should get UserAlreadyExistedException when sign up with existed name`() {
        whenever(signRepository.selectUserCountByName("existed"))
            .thenReturn(1)

        Assertions.assertThrows(UserAlreadyExistedException::class.java) {
            signService.signUp("existed", "password")
        }
    }
}
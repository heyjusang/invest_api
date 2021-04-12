package hey.jusang.invest

import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.exceptions.UserAlreadyExistedException
import hey.jusang.invest.exceptions.UserNotFoundException
import hey.jusang.invest.exceptions.WrongPasswordException
import hey.jusang.invest.entities.Investor
import hey.jusang.invest.models.InvestorDTO
import hey.jusang.invest.repositories.InvestorRepository
import hey.jusang.invest.services.SignServiceImpl
import hey.jusang.invest.utils.JwtTokenProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
class SignServiceTests {
    @Mock
    lateinit var investorRepository: InvestorRepository
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
        val data = Investor(1, "username", "encodedPassword", "USER")

        whenever(investorRepository.findByName("username"))
            .thenReturn(Optional.of(data))

        whenever(passwordEncoder.matches("password", "encodedPassword"))
            .thenReturn(true)

        whenever(jwtTokenProvider.createToken(InvestorDTO(data)))
            .thenReturn("TEST TOKEN")

        val token: String = signService.signIn("username", "password")
        assert(token == "TEST TOKEN")
    }

    @Test
    fun `we should sign up`() {
        whenever(investorRepository.countByName("username"))
            .thenReturn(0)

        whenever(passwordEncoder.encode("password"))
            .thenReturn("encodedPassword")

        val investor = Investor(null, "username", "encodedPassword", "USER")

        whenever(investorRepository.save(investor))
            .thenReturn(investor)

        val result: InvestorDTO = signService.signUp("username", "password")
        assert(InvestorDTO(investor) == result)
    }

    @Test
    fun `we should get UserNotFoundException when signing in with wrong name`() {
        whenever(investorRepository.findByName("wrong name"))
            .thenReturn(Optional.ofNullable(null))

        Assertions.assertThrows(UserNotFoundException::class.java) {
            signService.signIn("wrong name", "password")
        }
    }

    @Test
    fun `we should get WrongPasswordException when signing in with wrong password`() {
        val data = Investor(1, "username", "encodedPassword", "USER")

        whenever(investorRepository.findByName("username"))
            .thenReturn(Optional.of(data))

        whenever(passwordEncoder.matches("wrong password", "encodedPassword"))
            .thenReturn(false)

        Assertions.assertThrows(WrongPasswordException::class.java) {
            signService.signIn("username", "wrong password")
        }
    }

    @Test
    fun `we should get UserAlreadyExistedException when sign up with existed name`() {
        whenever(investorRepository.countByName("existed"))
            .thenReturn(1)

        Assertions.assertThrows(UserAlreadyExistedException::class.java) {
            signService.signUp("existed", "password")
        }
    }
}
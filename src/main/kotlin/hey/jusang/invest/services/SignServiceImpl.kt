package hey.jusang.invest.services

import hey.jusang.invest.exceptions.UserAlreadyExistedException
import hey.jusang.invest.exceptions.UserNotFoundException
import hey.jusang.invest.exceptions.WrongPasswordException
import hey.jusang.invest.models.User
import hey.jusang.invest.repositories.SignRepository
import hey.jusang.invest.utils.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class SignServiceImpl(
    val signRepository: SignRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtTokenProvider: JwtTokenProvider
) : SignService {
    override fun signIn(name: String, password: String): String {
        val user: User = signRepository.selectUserByName(name) ?: throw UserNotFoundException()

        if (!passwordEncoder.matches(password, user.password))
            throw WrongPasswordException()

        return jwtTokenProvider.createToken(user)
    }

    override fun signUp(name: String, password: String): Boolean {
        when {
            signRepository.selectUserCountByName(name) != 0 -> throw UserAlreadyExistedException()
            else -> return signRepository.insertUser(name, passwordEncoder.encode(password)) == 1
        }

    }
}
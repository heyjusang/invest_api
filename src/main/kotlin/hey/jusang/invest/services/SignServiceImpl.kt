package hey.jusang.invest.services

import hey.jusang.invest.entities.Investor
import hey.jusang.invest.exceptions.UserAlreadyExistedException
import hey.jusang.invest.exceptions.UserNotFoundException
import hey.jusang.invest.exceptions.WrongPasswordException
import hey.jusang.invest.models.InvestorDTO
import hey.jusang.invest.repositories.InvestorRepository
import hey.jusang.invest.utils.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class SignServiceImpl(
    val investorRepository: InvestorRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtTokenProvider: JwtTokenProvider
) : SignService {
    override fun signIn(name: String, password: String): String {
        val investor = investorRepository.findByName(name).orElseThrow { UserNotFoundException() }
        val investorDTO = InvestorDTO(investor)

        if (!passwordEncoder.matches(password, investorDTO.password))
            throw WrongPasswordException()

        return jwtTokenProvider.createToken(investorDTO)
    }

    override fun signUp(name: String, password: String): InvestorDTO {
        if (investorRepository.countByName(name) != 0L) throw UserAlreadyExistedException()

        val investorDTO = InvestorDTO(null, name, passwordEncoder.encode(password), "USER")
        val investor: Investor = investorDTO.toEntity()

        return InvestorDTO(investorRepository.save(investor))
    }
}
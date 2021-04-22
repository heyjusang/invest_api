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
    override fun signIn(investorDTO: InvestorDTO.Request): String {
        val investor = investorRepository.findByName(investorDTO.name).orElseThrow { UserNotFoundException() }

        if (!passwordEncoder.matches(investorDTO.password, investor.password))
            throw WrongPasswordException()

        return jwtTokenProvider.createToken(InvestorDTO.Data(investor))
    }

    override fun signUp(investorDTO: InvestorDTO.Request): InvestorDTO.Response {
        if (investorRepository.countByName(investorDTO.name) != 0L) throw UserAlreadyExistedException()

        investorDTO.encryptedPassword = passwordEncoder.encode(investorDTO.password)

        val investor: Investor = investorDTO.toEntity()

        return InvestorDTO.Response(investorRepository.save(investor))
    }
}
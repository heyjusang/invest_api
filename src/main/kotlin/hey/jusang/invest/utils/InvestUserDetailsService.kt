package hey.jusang.invest.utils

import hey.jusang.invest.entities.Investor
import hey.jusang.invest.exceptions.UserNotFoundException
import hey.jusang.invest.repositories.InvestorRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class InvestUserDetailsService(val investorRepository: InvestorRepository) : UserDetailsService {
    override fun loadUserByUsername(userId: String?): UserDetails {
        val investor: Investor = investorRepository.findById(userId!!.toLong()).orElseThrow { UserNotFoundException() }
        return InvestUserDetails(investor)
    }
}
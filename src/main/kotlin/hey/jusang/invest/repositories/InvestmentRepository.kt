package hey.jusang.invest.repositories

import hey.jusang.invest.entities.Investment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InvestmentRepository: JpaRepository<Investment, Long> {
    fun findAllByUserId(userId: Long): List<Investment>
}
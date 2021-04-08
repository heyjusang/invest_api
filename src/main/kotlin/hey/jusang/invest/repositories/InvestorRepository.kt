package hey.jusang.invest.repositories

import hey.jusang.invest.entities.Investor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InvestorRepository : JpaRepository<Investor, Long> {
    fun findByName(name: String): Optional<Investor>
    fun countByName(name: String): Long
}
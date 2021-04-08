package hey.jusang.invest.repositories

import hey.jusang.invest.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByStartedAtBeforeAndFinishedAtAfter(startedAt: LocalDateTime, finishedAt: LocalDateTime): List<Product>

    // TODO: row lock
    //override fun findById(id: Long): Optional<Product>
}
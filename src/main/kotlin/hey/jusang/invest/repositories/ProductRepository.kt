package hey.jusang.invest.repositories

import hey.jusang.invest.entities.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*
import javax.persistence.LockModeType

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByStartedAtBeforeAndFinishedAtAfter(
        startedAt: LocalDateTime,
        finishedAt: LocalDateTime,
        pageable: Pageable
    ): Slice<Product>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    fun findByIdForUpdate(id: Long): Optional<Product>
}
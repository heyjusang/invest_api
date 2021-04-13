package hey.jusang.invest.entities

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
@EntityListeners(AuditingEntityListener::class)
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @NotNull
    var title: String? = null,
    @PositiveOrZero
    var totalInvestingAmount: Int = 0,
    @PositiveOrZero
    var currentInvestingAmount: Int = 0,
    @PositiveOrZero
    var investorCount: Int = 0,
    @NotNull
    var startedAt: LocalDateTime? = null,
    @NotNull
    var finishedAt: LocalDateTime? = null
) {
    @CreatedDate
    var createdAt: LocalDateTime? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (id != (other as Product).id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
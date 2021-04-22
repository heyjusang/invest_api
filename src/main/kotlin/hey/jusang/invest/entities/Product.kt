package hey.jusang.invest.entities

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

@Entity
@EntityListeners(AuditingEntityListener::class)
class Product(
    @NotNull
    var title: String,
    @Positive
    var totalInvestingAmount: Int,
    @NotNull
    var startedAt: LocalDateTime,
    @NotNull
    var finishedAt: LocalDateTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @PositiveOrZero
    var currentInvestingAmount: Int = 0
    @PositiveOrZero
    var investorCount: Int = 0
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
package hey.jusang.invest.entities

import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
class Product() {
    // TODO: var, val, null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotNull
    var title: String? = null

    @PositiveOrZero
    var totalInvestingAmount: Int = 0

    @PositiveOrZero
    @ColumnDefault("0")
    // TODO: < totalInvestingAmount
    var currentInvestingAmount: Int = 0

    @PositiveOrZero
    @ColumnDefault("0")
    var investorCount: Int = 0

    @NotNull
    var startedAt: LocalDateTime? = null

    @NotNull
    var finishedAt: LocalDateTime? = null
    var createdAt: LocalDateTime? = null

    constructor(
        id: Long?, title: String?, totalInvestingAmount: Int, currentInvestingAmount: Int, investorCount: Int,
        startedAt: LocalDateTime?, finishedAt: LocalDateTime?
    ) : this() {
        this.id = id
        this.title = title
        this.totalInvestingAmount = totalInvestingAmount
        this.currentInvestingAmount = currentInvestingAmount
        this.investorCount = investorCount
        this.startedAt = startedAt
        this.finishedAt = finishedAt
    }
}
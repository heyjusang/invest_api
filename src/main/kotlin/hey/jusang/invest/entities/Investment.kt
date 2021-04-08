package hey.jusang.invest.entities

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Investment() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @NotNull
    var userId: Long = 0L
    @NotNull
    var productId: Long = 0L
    var amount: Int = 0
    var createdAt: LocalDateTime? = null

    @ManyToOne
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    var product: Product? = null

    constructor(id: Long?, userId: Long, productId: Long, amount: Int, createdAt: LocalDateTime?): this() {
        this.id = id
        this.userId = userId
        this.productId = productId
        this.amount = amount
        this.createdAt = createdAt
    }
}
package hey.jusang.invest.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@EntityListeners(AuditingEntityListener::class)
class Investment(
    @NotNull
    var userId: Long,
    @NotNull
    var productId: Long,
    var amount: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    @ManyToOne
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    var product: Product? = null
    @CreatedDate
    var createdAt: LocalDateTime? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (id != (other as Investment).id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
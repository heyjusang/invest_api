package hey.jusang.invest.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Investor() {
    // TODO : var -> setter ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    var password: String? = null
    var role: String? = null
    var createdAt: LocalDateTime? = null

    constructor(id: Long?, name: String?, password: String?, role: String?): this() {
        this.id = id
        this.name = name
        this.password = password
        this.role = role
    }
}

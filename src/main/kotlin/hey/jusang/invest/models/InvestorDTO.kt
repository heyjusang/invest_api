package hey.jusang.invest.models

import hey.jusang.invest.entities.Investor
import java.time.LocalDateTime

data class InvestorDTO(
    var id: Long? = null,
    var name: String? = null,
    var password: String? = null,
    var role: String? = null,
    var createdAt: LocalDateTime? = null
) {

    constructor(investor: Investor) : this() {
        id = investor.id
        name = investor.name
        password = investor.password
        role = investor.role
        createdAt = investor.createdAt
    }

    fun toEntity(): Investor {
        return Investor(id, name, password, role)
    }
}
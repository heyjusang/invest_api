package hey.jusang.invest.models

import hey.jusang.invest.entities.Investor

data class InvestorDTO(
    var id: Long? = null,
    var name: String? = null,
    var password: String? = null,
    var role: String? = null
) {
    constructor(investor: Investor) : this(investor.id, investor.name, investor.password, investor.role)

    fun toEntity(): Investor {
        return Investor(id, name, password, role)
    }
}
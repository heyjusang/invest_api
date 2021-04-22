package hey.jusang.invest.models

import hey.jusang.invest.entities.Investor

class InvestorDTO {
    data class Request(
        val name: String,
        val password: String
    ) {
        var encryptedPassword: String = ""
        var role: String = "USER"

        fun toEntity(): Investor {
            return Investor(name, encryptedPassword, role)
        }
    }

    data class Response(
        val id: Long? = null,
        val name: String,
        val role: String
    ) {
        constructor(investor: Investor) : this(investor.id, investor.name, investor.role)
    }

    data class Data(
        val id: Long,
        val name: String,
        val encryptedPassword: String,
        val role: String
    ) {
        constructor(investor: Investor) : this(investor.id!!, investor.name, investor.password, investor.role)
    }
}
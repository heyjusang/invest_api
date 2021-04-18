package hey.jusang.invest.models

import hey.jusang.invest.entities.Investment

data class InvestmentDTO(
    var id: Long? = null,
    var userId: Long = 0L,
    var productId: Long = 0L,
    var amount: Int = 0,
    var productDTO: ResponseProductDTO? = null
) {
    constructor(investment: Investment) : this() {
        id = investment.id
        userId = investment.userId
        productId = investment.productId
        amount = investment.amount
        productDTO = investment.product?.let { ResponseProductDTO(it) }
    }

    fun toEntity(): Investment {
        return Investment(id, userId, productId, amount)
    }
}
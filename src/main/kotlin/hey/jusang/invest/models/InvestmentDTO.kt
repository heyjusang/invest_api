package hey.jusang.invest.models

import hey.jusang.invest.entities.Investment

class InvestmentDTO {
    data class Request(
        val productId: Long,
        val amount: Int
    ) {
        var userId: Long = 0L

        fun toEntity(): Investment {
            return Investment(userId, productId, amount)
        }
    }

    data class Response(
        val id: Long? = null,
        var userId: Long = 0L,
        var productId: Long = 0L,
        var amount: Int = 0,
        var productDTO: ProductDTO.Response? = null
    ) {
        constructor(investment: Investment) : this(
            investment.id,
            investment.userId,
            investment.productId,
            investment.amount
        ) {
            productDTO = investment.product?.let { ProductDTO.Response(it) }
        }
    }
}
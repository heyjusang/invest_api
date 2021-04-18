package hey.jusang.invest.models

import hey.jusang.invest.entities.Product
import java.time.LocalDateTime

data class ResponseProductDTO(
    val id: Long?,
    val title: String,
    val totalInvestingAmount: Int,
    val currentInvestingAmount: Int,
    val investorCount: Int,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
    var soldOut: Boolean = false
) {
    constructor(product: Product) : this(
        product.id,
        product.title,
        product.totalInvestingAmount,
        product.currentInvestingAmount,
        product.investorCount,
        product.startedAt,
        product.finishedAt
    ) {
        soldOut = totalInvestingAmount == currentInvestingAmount
    }

}
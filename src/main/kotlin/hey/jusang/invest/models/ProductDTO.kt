package hey.jusang.invest.models

import hey.jusang.invest.entities.Product
import java.time.LocalDateTime

data class ProductDTO(
    var id: Long? = null,
    var title: String? = null,
    var totalInvestingAmount: Int = 0,
    var currentInvestingAmount: Int = 0,
    var investorCount: Int? = null,
    var startedAt: LocalDateTime? = null,
    var finishedAt: LocalDateTime? = null,
    var soldOut: Boolean? = null
) {
    constructor(product: Product) : this() {
        id = product.id
        title = product.title
        totalInvestingAmount = product.totalInvestingAmount
        currentInvestingAmount = product.currentInvestingAmount
        investorCount = product.investorCount
        startedAt = product.startedAt
        finishedAt = product.finishedAt
        soldOut = totalInvestingAmount == currentInvestingAmount
    }

    fun toEntity(): Product {
        return Product(
            id,
            title,
            totalInvestingAmount,
            currentInvestingAmount,
            totalInvestingAmount,
            startedAt,
            finishedAt
        )
    }
}
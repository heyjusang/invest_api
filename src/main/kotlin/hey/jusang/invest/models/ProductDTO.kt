package hey.jusang.invest.models

import hey.jusang.invest.entities.Product
import java.time.LocalDateTime

class ProductDTO {
    // TODO : case - request with null param
    data class Request(
        val title: String,
        val totalInvestingAmount: Int,
        val startedAt: LocalDateTime,
        val finishedAt: LocalDateTime
    ) {
        fun toEntity(): Product {
            return Product(title, totalInvestingAmount, startedAt, finishedAt)
        }
    }

    data class Response(
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
}
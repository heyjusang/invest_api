package hey.jusang.invest.models

import hey.jusang.invest.entities.Product
import java.time.LocalDateTime

// TODO : case - request with null param
data class CreateProductDTO(
    val title: String,
    val totalInvestingAmount: Int,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime
) {
    fun toEntity(): Product {
        return Product(title, totalInvestingAmount, startedAt, finishedAt)
    }
}
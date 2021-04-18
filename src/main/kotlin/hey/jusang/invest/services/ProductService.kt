package hey.jusang.invest.services

import hey.jusang.invest.models.ProductDTO
import java.time.LocalDateTime

interface ProductService {
    fun getProducts(): List<ProductDTO>
    fun createProduct(
        title: String, totalInvestingAmount: Int, startedAt: LocalDateTime, finishedAt: LocalDateTime
    ): ProductDTO
}
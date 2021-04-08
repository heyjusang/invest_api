package hey.jusang.invest.services

import hey.jusang.invest.entities.Investment
import hey.jusang.invest.entities.Product
import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.models.ProductDTO
import java.time.LocalDateTime

interface InvestmentService {
    fun getProducts(current: LocalDateTime): List<ProductDTO>
    fun getInvestments(userId: Long): List<InvestmentDTO>
    fun createInvestment(userId: Long, productId: Long, amount: Int): InvestmentDTO
}
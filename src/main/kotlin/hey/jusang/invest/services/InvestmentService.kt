package hey.jusang.invest.services

import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.models.ProductDTO

interface InvestmentService {
    fun getProducts(): List<ProductDTO>
    fun getInvestments(userId: Long): List<InvestmentDTO>
    fun createInvestment(userId: Long, productId: Long, amount: Int): InvestmentDTO
}
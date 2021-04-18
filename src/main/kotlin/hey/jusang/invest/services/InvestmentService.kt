package hey.jusang.invest.services

import hey.jusang.invest.models.InvestmentDTO

interface InvestmentService {
    fun getInvestments(userId: Long): List<InvestmentDTO>
    fun createInvestment(userId: Long, productId: Long, amount: Int): InvestmentDTO
}
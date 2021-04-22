package hey.jusang.invest.services

import hey.jusang.invest.models.InvestmentDTO

interface InvestmentService {
    fun getInvestments(userId: Long): List<InvestmentDTO.Response>
    fun createInvestment(userId: Long, investmentDTO: InvestmentDTO.Request): InvestmentDTO.Response
}
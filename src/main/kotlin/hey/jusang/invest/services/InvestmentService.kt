package hey.jusang.invest.services

import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product

interface InvestmentService {
    fun getProducts(): List<Product>
    fun getInvestments(userId: Int): List<Investment>
    fun createInvestment(userId: Int, productId: Int, amount: Int): Boolean
}
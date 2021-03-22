package hey.jusang.invest.services

import hey.jusang.invest.exceptions.*
import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import hey.jusang.invest.repositories.InvestmentRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class InvestmentServiceImpl(val investmentRepository: InvestmentRepository) : InvestmentService {
    override fun getProducts(): List<Product> {
        return investmentRepository.selectProducts()
    }

    override fun getInvestments(userId: Int): List<Investment> {
        return investmentRepository.selectInvestments(userId)
    }

    @Transactional
    override fun createInvestment(userId: Int, productId: Int, amount: Int): Boolean {
        if (amount <= 0) throw InvalidAmountException()

        val current: LocalDateTime = LocalDateTime.now()
        val product: Product =
            investmentRepository.selectProductForUpdate(productId) ?: throw ProductNotFoundException()

        if (product.startedAt > current) {
            throw ProductNotOpenedException()
        }

        if (product.finishedAt <= current) {
            throw ProductClosedException()
        }

        if (product.totalInvestingAmount < product.currentInvestingAmount + amount) {
            throw TotalInvestingAmountExceededException()
        }

        val success = investmentRepository.insertInvestment(userId, amount, productId)
        val count = investmentRepository.updateProduct(amount, productId)

        return count == 1 && success == 1
    }
}
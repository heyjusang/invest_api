package hey.jusang.invest.services

import hey.jusang.invest.exceptions.*
import hey.jusang.invest.entities.Investment
import hey.jusang.invest.entities.Product
import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.repositories.InvestmentRepository
import hey.jusang.invest.repositories.ProductRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime

@Component
class InvestmentServiceImpl(
    val investmentRepository: InvestmentRepository,
    val productRepository: ProductRepository,
    val clock: Clock
) : InvestmentService {
    override fun getInvestments(userId: Long): List<InvestmentDTO.Response> {
        return investmentRepository.findAllByUserId(userId)
            .map { InvestmentDTO.Response(it) }
    }

    @Transactional
    override fun createInvestment(userId: Long, investmentDTO: InvestmentDTO.Request): InvestmentDTO.Response {
        if (investmentDTO.amount <= 0) throw InvalidAmountException()

        val current: LocalDateTime = LocalDateTime.now(clock)
        val product: Product =
            productRepository.findByIdForUpdate(investmentDTO.productId).orElseThrow { ProductNotFoundException() }

        if (product.startedAt > current) {
            throw ProductNotOpenedException()
        }

        if (product.finishedAt <= current) {
            throw ProductClosedException()
        }

        if (product.totalInvestingAmount < product.currentInvestingAmount + investmentDTO.amount) {
            throw TotalInvestingAmountExceededException()
        }

        investmentDTO.userId = userId

        val investment: Investment = investmentDTO.toEntity()
        val result: Investment = investmentRepository.save(investment)

        // TODO : can save using product entity of investment entity ???
        product.currentInvestingAmount += investmentDTO.amount
        product.investorCount += 1
        productRepository.save(product)

        return InvestmentDTO.Response(result)
    }
}
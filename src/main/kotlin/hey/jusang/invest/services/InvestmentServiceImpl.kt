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
    override fun getInvestments(userId: Long): List<InvestmentDTO> {
        return investmentRepository.findAllByUserId(userId)
            .map { InvestmentDTO(it) }
    }

    @Transactional
    override fun createInvestment(userId: Long, productId: Long, amount: Int): InvestmentDTO {
        if (amount <= 0) throw InvalidAmountException()

        val current: LocalDateTime = LocalDateTime.now(clock)
        val product: Product = productRepository.findByIdForUpdate(productId).orElseThrow { ProductNotFoundException() }

        if (product.startedAt > current) {
            throw ProductNotOpenedException()
        }

        if (product.finishedAt <= current) {
            throw ProductClosedException()
        }

        if (product.totalInvestingAmount < product.currentInvestingAmount + amount) {
            throw TotalInvestingAmountExceededException()
        }

        val investmentDTO = InvestmentDTO()
        investmentDTO.userId = userId
        investmentDTO.productId = productId
        investmentDTO.amount = amount

        val investment: Investment = investmentDTO.toEntity()

        investmentRepository.save(investment)

        product.currentInvestingAmount += amount
        product.investorCount += 1
        productRepository.save(product)

        return investmentDTO
    }
}
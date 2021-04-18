package hey.jusang.invest.services

import hey.jusang.invest.entities.Product
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.repositories.ProductRepository
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class ProductServiceImpl(val productRepository: ProductRepository, val clock: Clock) : ProductService {
    override fun getProducts(): List<ProductDTO> {
        val current = LocalDateTime.now(clock)
        return productRepository.findAllByStartedAtBeforeAndFinishedAtAfter(current, current)
            .map { ProductDTO(it) }
    }

    override fun createProduct(
        title: String, totalInvestingAmount: Int, startedAt: LocalDateTime, finishedAt: LocalDateTime
    ): ProductDTO {
        // TODO: validation
        val productDTO = ProductDTO().apply {
            this.title = title
            this.totalInvestingAmount = totalInvestingAmount
            this.currentInvestingAmount = 0
            this.investorCount = 0
            this.startedAt = startedAt
            this.finishedAt = finishedAt
            this.soldOut = false
        }

        val product: Product = productDTO.toEntity()
        val result: Product = productRepository.save(product)

        return ProductDTO(result)
    }
}
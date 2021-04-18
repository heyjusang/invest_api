package hey.jusang.invest.services

import hey.jusang.invest.entities.Product
import hey.jusang.invest.exceptions.InvalidInvestingPeriodException
import hey.jusang.invest.exceptions.InvalidProductTitleException
import hey.jusang.invest.exceptions.InvalidTotalInvestingAmountException
import hey.jusang.invest.models.CreateProductDTO
import hey.jusang.invest.models.ResponseProductDTO
import hey.jusang.invest.repositories.ProductRepository
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class ProductServiceImpl(val productRepository: ProductRepository, val clock: Clock) : ProductService {
    override fun getProducts(): List<ResponseProductDTO> {
        val current = LocalDateTime.now(clock)
        return productRepository.findAllByStartedAtBeforeAndFinishedAtAfter(current, current)
            .map { ResponseProductDTO(it) }
    }

    override fun createProduct(productDTO: CreateProductDTO): ResponseProductDTO {
        if (productDTO.title.isBlank()) throw InvalidProductTitleException()

        if (productDTO.totalInvestingAmount <= 0) throw InvalidTotalInvestingAmountException()

        if (productDTO.startedAt >= productDTO.finishedAt) throw InvalidInvestingPeriodException()

        val product: Product = productDTO.toEntity()
        val result: Product = productRepository.save(product)

        return ResponseProductDTO(result)
    }
}
package hey.jusang.invest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.exceptions.*
import hey.jusang.invest.entities.Investment
import hey.jusang.invest.entities.Product
import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.repositories.InvestmentRepository
import hey.jusang.invest.repositories.ProductRepository
import hey.jusang.invest.services.InvestmentServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@ExtendWith(SpringExtension::class)
class InvestmentServiceTests {

    @Mock
    lateinit var investmentRepository: InvestmentRepository

    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var investmentService: InvestmentServiceImpl

    @Test
    fun `mock should be configured`() {
    }

    @Test
    fun `we should get products`() {
        val current = LocalDateTime.now()
        val data: List<Product> = listOf(
            Product(
                1, "product 1", 400000, 10000, 1,
                LocalDateTime.of(2020, Month.MARCH, 10, 11, 11, 11),
                LocalDateTime.of(2022, Month.MARCH, 15, 11, 11, 11),
            ),
            Product(
                5, "product 5", 500000, 20000, 1,
                LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11),
            )
        )

        whenever(productRepository.findAllByStartedAtBeforeAndFinishedAtAfter(current, current)).thenReturn(data)

        val products: List<ProductDTO> = investmentService.getProducts(current)
        assert(products == data.map{ ProductDTO(it)})
    }

    @Test
    fun `we should get investments of user by user id`() {
        val data: List<Investment> = listOf(
            Investment(
                4, 1, 1, 10000,
                LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11)
            ),
            Investment(
                15, 1, 33, 45000,
                LocalDateTime.of(2021, Month.MARCH, 10, 11, 12, 11)
            )
        )

        whenever(investmentRepository.findAllByUserId(1)).thenReturn(data)

        val investments: List<InvestmentDTO> = investmentService.getInvestments(1)

        assert(investments == data.map { InvestmentDTO(it) })
    }

    @Test
    fun `we should create investment`() {
        // TODO: investment equals(), hashcode()
        /*
        whenever(productRepository.findById(5))
            .thenReturn(
                Optional.of(
                    Product(
                        5, "product 5", 500000, 20000, 1,
                        LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                        LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11)
                    )
                )
            )

        whenever(investmentRepository.insertInvestment(1, 10, 5))
            .thenReturn(1)

        whenever(investmentRepository.updateProduct(10, 5))
            .thenReturn(1)

        val success: Boolean = investmentService.createInvestment(1, 5, 10)
        assert(success)
         */
    }

    @Test
    fun `we should get InvalidAmountException while creating investment with invalid amount`() {
        Assertions.assertThrows(InvalidAmountException::class.java) {
            investmentService.createInvestment(1, 5, -100)
        }
    }

    @Test
    fun `we should get ProductNotFoundException while creating investment with invalid product id`() {
        whenever(productRepository.findById(9999))
            .thenReturn(Optional.ofNullable(null))

        Assertions.assertThrows(ProductNotFoundException::class.java) {
            investmentService.createInvestment(1, 9999, 100)
        }
    }

    @Test
    fun `we should get ProductNotOpenedException while creating investment with not opened product`() {
        whenever(productRepository.findById(5))
            .thenReturn(
                Optional.of(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2022, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11)
                )
                )
            )

        Assertions.assertThrows(ProductNotOpenedException::class.java) {
            investmentService.createInvestment(1, 5, 100)
        }
    }

    @Test
    fun `we should get ProductClosedException while creating investment with closed product`() {
        whenever(productRepository.findById(5))
            .thenReturn(
                Optional.of(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2020, Month.MARCH, 21, 5, 11, 11)
                )
                )
            )

        Assertions.assertThrows(ProductClosedException::class.java) {
            investmentService.createInvestment(1, 5, 100)
        }
    }

    @Test
    fun `we should get TotalInvestingAmountExceededException while creating investment with exceeded amount`() {
        whenever(productRepository.findById(5))
            .thenReturn(
                Optional.of(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11)
                )
                )
            )

        Assertions.assertThrows(TotalInvestingAmountExceededException::class.java) {
            investmentService.createInvestment(1, 5, 1000000)
        }
    }
}

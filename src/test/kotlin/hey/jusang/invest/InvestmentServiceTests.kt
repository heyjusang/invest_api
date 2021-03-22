package hey.jusang.invest

import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.exceptions.*
import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import hey.jusang.invest.repositories.InvestmentRepository
import hey.jusang.invest.services.InvestmentServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.time.Month

@ExtendWith(SpringExtension::class)
class InvestmentServiceTests {

    @Mock
    lateinit var investmentRepository: InvestmentRepository

    @InjectMocks
    lateinit var investmentService: InvestmentServiceImpl

    @Test
    fun `mock should be configured`() {
    }

    @Test
    fun `we should get products`() {
        val data: List<Product> = listOf(
            Product(
                1, "product 1", 400000, 10000, 1,
                LocalDateTime.of(2020, Month.MARCH, 10, 11, 11, 11),
                LocalDateTime.of(2022, Month.MARCH, 15, 11, 11, 11), 'N'
            ),
            Product(
                5, "product 5", 500000, 20000, 1,
                LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11), 'N'
            )
        )

        whenever(investmentRepository.selectProducts()).thenReturn(data)

        val products: List<Product> = investmentService.getProducts()

        assert(products == data)
    }

    @Test
    fun `we should get investments of user by user id`() {
        val data: List<Investment> = listOf(
            Investment(
                4, 1, 1, "product 1", 2000000, 10000,
                LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11)
            ),
            Investment(
                15, 1, 33, "product 33", 3000000, 45000,
                LocalDateTime.of(2021, Month.MARCH, 10, 11, 12, 11)
            )
        )

        whenever(investmentRepository.selectInvestments(1)).thenReturn(data)

        val investments: List<Investment> = investmentService.getInvestments(1)

        assert(investments == data)
    }

    @Test
    fun `we should create investment`() {
        whenever(investmentRepository.selectProductForUpdate(5))
            .thenReturn(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11), 'N'
                )
            )

        whenever(investmentRepository.insertInvestment(1, 10, 5))
            .thenReturn(1)

        whenever(investmentRepository.updateProduct(10, 5))
            .thenReturn(1)

        val success: Boolean = investmentService.createInvestment(1, 5, 10)
        assert(success)
    }

    @Test
    fun `we should get InvalidAmountException while creating investment with invalid amount`() {
        Assertions.assertThrows(InvalidAmountException::class.java) {
            investmentService.createInvestment(1, 5, -100)
        }
    }

    @Test
    fun `we should get ProductNotFoundException while creating investment with invalid product id`() {
        whenever(investmentRepository.selectProductForUpdate(9999))
            .thenReturn(null)

        Assertions.assertThrows(ProductNotFoundException::class.java) {
            investmentService.createInvestment(1, 9999, 100)
        }
    }

    @Test
    fun `we should get ProductNotOpenedException while creating investment with not opened product`() {
        whenever(investmentRepository.selectProductForUpdate(5))
            .thenReturn(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2022, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11), 'N'
                )
            )

        Assertions.assertThrows(ProductNotOpenedException::class.java) {
            investmentService.createInvestment(1, 5, 100)
        }
    }

    @Test
    fun `we should get ProductClosedException while creating investment with closed product`() {
        whenever(investmentRepository.selectProductForUpdate(5))
            .thenReturn(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2020, Month.MARCH, 21, 5, 11, 11), 'N'
                )
            )

        Assertions.assertThrows(ProductClosedException::class.java) {
            investmentService.createInvestment(1, 5, 100)
        }
    }

    @Test
    fun `we should get TotalInvestingAmountExceededException while creating investment with exceeded amount`() {
        whenever(investmentRepository.selectProductForUpdate(5))
            .thenReturn(
                Product(
                    5, "product 5", 500000, 20000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11), 'N'
                )
            )

        Assertions.assertThrows(TotalInvestingAmountExceededException::class.java) {
            investmentService.createInvestment(1, 5, 1000000)
        }
    }
}

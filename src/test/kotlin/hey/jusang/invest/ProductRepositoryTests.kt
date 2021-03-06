package hey.jusang.invest

import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.entities.Product
import hey.jusang.invest.repositories.ProductRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.Clock
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

@DataJpaTest
class ProductRepositoryTests {
    @Autowired
    lateinit var testRepository: ProductRepository

    @Mock
    lateinit var clock: Clock

    @BeforeEach
    fun setup() {
        val fixedClock: Clock = Clock.fixed(
            LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11).atZone(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        )

        whenever(clock.instant()).thenReturn(fixedClock.instant())
        whenever(clock.zone).thenReturn(fixedClock.zone)
    }

    @Test
    fun `testRepository should be configured`() {
    }

    @Test
    fun `we should get 23 products (including 1 sold out) when requesting a list of product within the period`() {
        val current: LocalDateTime = LocalDateTime.now(clock)
        var soldOut = 0
        val pageable: Pageable = PageRequest.of(0, 30)

        val products: Slice<Product> =
            testRepository.findAllByStartedAtBeforeAndFinishedAtAfter(current, current, pageable)
        assert(products.content.size == 23)

        for (product in products.content) {
            assert(current > product.startedAt && current < product.finishedAt)

            if (product.totalInvestingAmount == product.currentInvestingAmount) {
                soldOut++
            }
        }

        assert(soldOut == 1)
    }

    @Test
    fun `we should get product with an ID of 1`() {
        val product: Product = testRepository.findById(1).get()
        assert(product.id == 1L)
    }

    @Test
    fun `we cannot get product without proper ID`() {
        val empty: Boolean = testRepository.findById(9999).isEmpty
        assert(empty)
    }

    @Test
    fun `we should update product with an ID of 1`() {
        val product: Product = testRepository.findById(1).get()
        product.currentInvestingAmount += 100
        product.investorCount += 1

        val updated: Product = testRepository.save(product)

        assert(updated.id == 1L)
        assert(updated.currentInvestingAmount == 100)
        assert(updated.investorCount == 1)
    }

    @Test
    fun `we cannot update product over total investing amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            val product: Product = testRepository.findById(1).get()
            product.currentInvestingAmount += 50000000
            testRepository.saveAndFlush(product)
        }
    }

    @Test
    fun `we should create product`() {
        val product = Product(
            "product name",
            10000,
            LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11),
            LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11)
        )

        testRepository.save(product)
        // TODO: check success
    }

    @Test
    fun `we cannot create product with negative total investing amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            val product = Product(
                "product name",
                -10000,
                LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11),
                LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11)
            )

            testRepository.save(product)
        }
    }

    @Test
    fun `we cannot create product with wrong range date`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            val product = Product(
                "product name",
                10000,
                LocalDateTime.of(2022, Month.MARCH, 10, 11, 11, 11),
                LocalDateTime.of(2021, Month.APRIL, 10, 11, 11, 11)
            )

            testRepository.save(product)
        }
    }
}
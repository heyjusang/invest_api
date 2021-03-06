package hey.jusang.invest

import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.entities.Product
import hey.jusang.invest.exceptions.InvalidInvestingPeriodException
import hey.jusang.invest.exceptions.InvalidProductTitleException
import hey.jusang.invest.exceptions.InvalidTotalInvestingAmountException
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.repositories.ProductRepository
import hey.jusang.invest.services.ProductServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Clock
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

@ExtendWith(SpringExtension::class)
class   ProductServiceTests {
    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var clock: Clock

    @InjectMocks
    lateinit var productService: ProductServiceImpl

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
    fun `mock should be configured`() {
    }

    @Test
    fun `we should get products`() {
        val pageable: Pageable = PageRequest.of(0, 30)
        val fixedNow = LocalDateTime.now(clock)
        val data: Slice<Product> = SliceImpl(listOf(
            Product("product 1", 400000,
                LocalDateTime.of(2020, Month.MARCH, 10, 11, 11, 11),
                LocalDateTime.of(2022, Month.MARCH, 15, 11, 11, 11),
            ),
            Product("product 5", 500000,
                LocalDateTime.of(2020, Month.MARCH, 20, 12, 11, 11),
                LocalDateTime.of(2022, Month.MARCH, 21, 5, 11, 11),
            )
        ))

        whenever(productRepository.findAllByStartedAtBeforeAndFinishedAtAfter(fixedNow, fixedNow, pageable)).thenReturn(data)

        val products: Slice<ProductDTO.Response> = productService.getProducts(pageable)
        assert(products.content == data.content.map { ProductDTO.Response(it) })
    }

    @Test
    fun `we should create product`() {
        val product = Product(
            "product name",
            10000,
            LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11),
            LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11)
        )

        whenever(productRepository.save(product)).thenReturn(product)

        val productDTO: ProductDTO.Response = productService.createProduct(
            ProductDTO.Request(
                "product name",
                10000,
                LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11),
                LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11)
            )
        )

        assert(productDTO == ProductDTO.Response(product))
    }

    @Test
    fun `we should get InvalidProductTitleException while creating product with invalid title`() {
        Assertions.assertThrows(InvalidProductTitleException::class.java) {
            productService.createProduct(
                ProductDTO.Request(
                    "",
                    10000,
                    LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11),
                    LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11)
                )
            )
        }
    }

    @Test
    fun `we should get InvalidTotalInvestingAmountException while creating product with invalid total amount`() {
        Assertions.assertThrows(InvalidTotalInvestingAmountException::class.java) {
            productService.createProduct(
                ProductDTO.Request(
                    "product name",
                    -10000,
                    LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11),
                    LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11)
                )
            )
        }
    }

    @Test
    fun `we should get InvalidInvestingPeriodException while creating product with invalid investing period`() {
        Assertions.assertThrows(InvalidInvestingPeriodException::class.java) {
            productService.createProduct(
                ProductDTO.Request(
                    "product name",
                    10000,
                    LocalDateTime.of(2022, Month.APRIL, 10, 11, 11, 11),
                    LocalDateTime.of(2021, Month.MARCH, 10, 11, 11, 11)
                )
            )
        }
    }
}
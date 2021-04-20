package hey.jusang.invest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.controllers.ProductController
import hey.jusang.invest.exceptions.ErrorCode
import hey.jusang.invest.exceptions.InvalidInvestingPeriodException
import hey.jusang.invest.exceptions.InvalidProductTitleException
import hey.jusang.invest.exceptions.InvalidTotalInvestingAmountException
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.services.ProductService
import hey.jusang.invest.utils.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.Month

@WebMvcTest(ProductController::class)
class ProductControllerTests {
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var productService: ProductService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())
    private val time1: LocalDateTime = LocalDateTime.of(2020, Month.MARCH, 10, 11, 11, 11)
    private val time2: LocalDateTime = LocalDateTime.of(2022, Month.MARCH, 15, 11, 11, 11)

    @Test
    fun `mock mvc should be configured`() {
    }

    @Test
    fun `we should get products`() {
        val data: List<ProductDTO.Response> = listOf(
            ProductDTO.Response(
                1, "product 1", 400000, 10000, 1, time1, time2, false
            ),
            ProductDTO.Response(
                5, "product 5", 500000, 20000, 1, time1, time2, false
            )
        )

        whenever(productService.getProducts()).thenReturn(data)

        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<ProductDTO.Response> = objectMapper.readValue(content)

        assert(products == data)
    }

    @Test
    fun `we should handle SQLException while getting products with database problem`() {
        whenever(productService.getProducts())
            .thenAnswer { throw SQLException("error message") }

        getProducts()
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should create product`() {
        val data = ProductDTO.Response(
            1, "product name", 10000, 0, 0, time1, time2, false
        )

        whenever(productService.createProduct(ProductDTO.Request("product name", 10000, time1, time2))).thenReturn(data)

        val resultActions: ResultActions = createProduct(1, "product name", 10000, time1, time2)
        resultActions.andExpect(status().isCreated)

        val content: String = resultActions.andReturn().response.contentAsString
        val product: ProductDTO.Response = objectMapper.readValue(content)

        assert(product == data)
    }

    @Test
    @WithAnonymousUser
    fun `we cannot create product without authentication`() {
        whenever(productService.createProduct(ProductDTO.Request("product name", 10000, time1, time2)))
            .thenReturn(ProductDTO.Response(1, "product name", 10000, 0, 0, time1, time2, false))

        createProduct(1, "product name", 10000, time1, time2)
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle SQLException while creating product with database problem`() {
        whenever(productService.createProduct(ProductDTO.Request("product name", 1000, time1, time2)))
            .thenAnswer { throw SQLException("error message") }

        createProduct(1, "product name", 1000, time1, time2)
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle InvalidProductTitleException while creating product with invalid title`() {
        whenever(productService.createProduct(ProductDTO.Request("", 10000, time1, time2)))
            .thenAnswer { throw InvalidProductTitleException() }

        createProduct(1, "", 10000, time1, time2)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_PRODUCT_TITLE))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle InvalidTotalInvestingAmountException while creating product with invalid total amount`() {
        whenever(productService.createProduct(ProductDTO.Request("product name", -10000, time1, time2)))
            .thenAnswer { throw InvalidTotalInvestingAmountException() }

        createProduct(1, "product name", -10000, time1, time2)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_TOTAL_INVESTING_AMOUNT))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle InvalidInvestingPeriodException while creating product with invalid investing period`() {
        whenever(productService.createProduct(ProductDTO.Request("product name", 10000, time2, time1)))
            .thenAnswer { throw InvalidInvestingPeriodException() }

        createProduct(1, "product name", 10000, time2, time1)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INVESTING_PERIOD))
    }

    private fun createProduct(
        userId: Long,
        title: String,
        totalInvestingAmount: Int,
        startedAt: LocalDateTime,
        finishedAt: LocalDateTime
    ): ResultActions {
        return mvc.perform(
            MockMvcRequestBuilders.post("/product")
                .header("X-USER-ID", userId)
                .param("title", title)
                .param("totalInvestingAmount", totalInvestingAmount.toString())
                .param("startedAt", startedAt.toString())
                .param("finishedAt", finishedAt.toString())
        )
    }

    private fun getProducts(): ResultActions {
        return mvc.perform(MockMvcRequestBuilders.get("/products"))
    }
}
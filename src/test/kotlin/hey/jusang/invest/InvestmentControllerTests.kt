package hey.jusang.invest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.controllers.InvestmentController
import hey.jusang.invest.exceptions.*
import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import hey.jusang.invest.services.InvestmentService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.Month

@WebMvcTest(InvestmentController::class)
class InvestmentControllerTests {
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var investmentService: InvestmentService

    var objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())

    @Test
    fun `mock mvc should be configured`() {
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

        whenever(investmentService.getProducts()).thenReturn(data)

        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<Product> = objectMapper.readValue(content)

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

        whenever(investmentService.getInvestments(1)).thenReturn(data)

        val resultActions: ResultActions = getInvestments(1)
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val investments: List<Investment> = objectMapper.readValue(content)

        assert(investments == data)
    }

    @Test
    fun `we should create investment`() {
        whenever(investmentService.createInvestment(1, 1, 10000))
            .thenReturn(true)

        createInvestment(1, 1, 10000)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    fun `we should handle SQLException while getting products with database problem`() {
        whenever(investmentService.getProducts())
            .thenAnswer { throw SQLException("error message") }

        getProducts()
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    fun `we should handle SQLException while getting investments with database problem`() {
        whenever(investmentService.getInvestments(1))
            .thenAnswer { throw SQLException("error message") }

        getInvestments(1)
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    fun `we should handle SQLException while creating investment with database problem`() {
        whenever(investmentService.createInvestment(1, 1, 10000))
            .thenAnswer { throw SQLException("error message") }

        createInvestment(1, 1, 10000)
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    fun `we should handle InvalidAmountException while creating investment with invalid amount`() {
        whenever(investmentService.createInvestment(1, 1, -1))
            .thenAnswer { throw InvalidAmountException() }

        createInvestment(1, 1, -1)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_AMOUNT))
    }

    @Test
    fun `we should handle ProductNotFoundException while creating investment with invalid product id`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw ProductNotFoundException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_FOUND))
    }

    @Test
    fun `we should handle ProductNotOpenedException while creating investment with not opened product`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw ProductNotOpenedException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_OPENED))
    }

    @Test
    fun `we should handle ProductClosedException while creating investment with closed product`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw ProductClosedException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_CLOSED))
    }

    @Test
    fun `we should handle TotalInvestingAmountExceededException while creating investment with exceeded amount`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw TotalInvestingAmountExceededException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.TOTAL_INVESTING_AMOUNT_EXCEEDED))
    }

    private fun createInvestment(userId: Int, productId: Int, amount: Int): ResultActions {
        return mvc.perform(
            post("/investment")
                .header("X-USER-ID", userId)
                .param("product_id", productId.toString())
                .param("amount", amount.toString())
        )
    }

    private fun getInvestments(userId: Int): ResultActions {
        return mvc.perform(get("/investments").header("X-USER-ID", userId))
    }

    private fun getProducts(): ResultActions {
        return mvc.perform(get("/products"))
    }
}

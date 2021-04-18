package hey.jusang.invest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.whenever
import hey.jusang.invest.controllers.InvestmentController
import hey.jusang.invest.exceptions.*
import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.services.InvestmentService
import hey.jusang.invest.utils.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
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

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())

    @Test
    fun `mock mvc should be configured`() {
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should get investments of user by user id`() {
        val data: List<InvestmentDTO> = listOf(
            InvestmentDTO(
                4, 1, 1, 10000,
                ProductDTO(
                    1, "product 1", 2000000, 10000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 10, 11, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 15, 11, 11, 11), false
                )
            ),
            InvestmentDTO(
                15, 1, 33, 45000,
                ProductDTO(
                    33, "product 33", 3000000, 45000, 1,
                    LocalDateTime.of(2020, Month.MARCH, 10, 11, 11, 11),
                    LocalDateTime.of(2022, Month.MARCH, 15, 11, 11, 11), false
                )
            )
        )

        whenever(investmentService.getInvestments(1)).thenReturn(data)

        val resultActions: ResultActions = getInvestments(1)
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val investments: List<InvestmentDTO> = objectMapper.readValue(content)

        assert(investments == data)
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should create investment`() {
        val data = InvestmentDTO(1, 1, 1, 10000)
        whenever(investmentService.createInvestment(1, 1, 10000))
            .thenReturn(data)

        val resultActions: ResultActions = createInvestment(1, 1, 10000)
        resultActions.andExpect(status().isCreated)

        val content: String = resultActions.andReturn().response.contentAsString
        val investment: InvestmentDTO = objectMapper.readValue(content)

        assert(investment == data)
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle SQLException while getting investments with database problem`() {
        whenever(investmentService.getInvestments(1))
            .thenAnswer { throw SQLException("error message") }

        getInvestments(1)
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle SQLException while creating investment with database problem`() {
        whenever(investmentService.createInvestment(1, 1, 10000))
            .thenAnswer { throw SQLException("error message") }

        createInvestment(1, 1, 10000)
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.message").value("error message"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle InvalidAmountException while creating investment with invalid amount`() {
        whenever(investmentService.createInvestment(1, 1, -1))
            .thenAnswer { throw InvalidAmountException() }

        createInvestment(1, 1, -1)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_AMOUNT))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle ProductNotFoundException while creating investment with invalid product id`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw ProductNotFoundException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_FOUND))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle ProductNotOpenedException while creating investment with not opened product`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw ProductNotOpenedException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_OPENED))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle ProductClosedException while creating investment with closed product`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw ProductClosedException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_CLOSED))
    }

    @Test
    @WithMockUser(username = "1")
    fun `we should handle TotalInvestingAmountExceededException while creating investment with exceeded amount`() {
        whenever(investmentService.createInvestment(1, 1, 100))
            .thenAnswer { throw TotalInvestingAmountExceededException() }

        createInvestment(1, 1, 100)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.TOTAL_INVESTING_AMOUNT_EXCEEDED))
    }

    @Test
    @WithAnonymousUser
    fun `we cannot create investment without authentication`() {
        whenever(investmentService.createInvestment(1, 1, 10000))
            .thenReturn(InvestmentDTO(1, 1, 1, 10000))

        createInvestment(1, 1, 10000)
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "2")
    fun `we cannot create investment of others`() {
        whenever(investmentService.createInvestment(1, 1, 10000))
            .thenReturn(InvestmentDTO(1, 1, 1, 10000))

        createInvestment(1, 1, 10000)
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.FORBIDDEN_REQUEST))
    }

    @Test
    @WithAnonymousUser
    fun `we cannot get investments without authentication`() {
        val data: List<InvestmentDTO> = listOf(
            InvestmentDTO(4, 1, 1, 10000)
        )

        whenever(investmentService.getInvestments(1)).thenReturn(data)

        getInvestments(1)
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "2")
    fun `we cannot get investments of others`() {
        val data: List<InvestmentDTO> = listOf(
            InvestmentDTO(4, 1, 1, 10000)
        )

        whenever(investmentService.getInvestments(1)).thenReturn(data)

        getInvestments(1)
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.FORBIDDEN_REQUEST))
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
}

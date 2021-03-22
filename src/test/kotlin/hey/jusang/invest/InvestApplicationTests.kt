package hey.jusang.invest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import hey.jusang.invest.exceptions.ErrorCode
import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SqlGroup(
    value = [
        Sql(
            scripts = ["/data/create_db.sql", "/data/insert_test_data.sql"],
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        Sql(
            scripts = ["/data/drop_db.sql"],
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
    ]
)
class InvestApplicationTests {
    /*
     * product 1: Normal Product
     * product 2: Closed Product
     * product 3: Sold-out Product - investor: user 10, user 11
     * product 4: Last chance Product (remaining amount is 1) - investor: user10, user 12
     * product 5: Not opened Product
     * product 6~25: Dummy Normal Product
     */

    val normalProductId = 1
    val closedProductId = 2
    val soldOutProductId = 3
    val lastChanceProductId = 4
    val notOpenedProductId = 5
    val invalidProductId = 9999

    val investorId = 10
    val newInvestorId = 99

    @Autowired
    lateinit var mvc: MockMvc

    var objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())

    @Test
    fun `application should be configured`() {
    }

    @Test
    fun `we should get 23 products (including 1 sold out) when requesting a list of product within the period`() {
        val current: LocalDateTime = LocalDateTime.now()
        var soldOut = 0
        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<Product> = objectMapper.readValue(content)

        assert(products.size == 23)

        for (product in products) {
            assert(current >= product.startedAt && current < product.finishedAt)

            if (product.soldOut == 'Y') {
                soldOut++
                assert(product.totalInvestingAmount == product.currentInvestingAmount)
            } else {
                assert(product.totalInvestingAmount > product.currentInvestingAmount)
            }
        }

        assert(soldOut == 1)
    }

    @Test
    fun `we should get 2 investments when requesting a list of investment for user with an ID of 10`() {
        val resultActions: ResultActions = getInvestments(investorId)
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val investments: List<Investment> = objectMapper.readValue(content)

        assert(investments.size == 2)

        for (investment in investments) {
            assert(investment.amount > 0)
            assert(investment.userId == investorId)
        }
    }

    @Test
    fun `we should create investment and get 1 investment when requesting a list of investment`() {
        createInvestment(newInvestorId, normalProductId, 10000)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.success").value(true))

        val resultActions: ResultActions = getInvestments(newInvestorId)
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val investments: List<Investment> = objectMapper.readValue(content)

        assert(investments.size == 1)
        assert(investments[0].amount == 10000)
        assert(investments[0].productId == normalProductId)
        assert(investments[0].userId == newInvestorId)
    }

    @Test
    fun `we should create investment and updated product should be returned when request a list of product`() {
        createInvestment(newInvestorId, lastChanceProductId, 1)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.success").value(true))

        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<Product> = objectMapper.readValue(content)

        val updatedProducts: List<Product> = products.stream().filter { it.id == lastChanceProductId }.toList()
        assert(updatedProducts.size == 1)
        assert(updatedProducts[0].currentInvestingAmount == 2000000)
        assert(updatedProducts[0].investorCount == 3)
        assert(updatedProducts[0].soldOut == 'Y')
    }

    @Test
    fun `we cannot create investment with invalid amount`() {
        createInvestment(newInvestorId, normalProductId, -1)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_AMOUNT))

        checkInvestmentNotCreated(newInvestorId)
        checkProductNotUpdated(normalProductId)
    }

    @Test
    fun `we cannot create investment with invalid product id`() {
        createInvestment(newInvestorId, invalidProductId, 10000)
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_FOUND))

        checkInvestmentNotCreated(newInvestorId)
        checkProductNotUpdated(invalidProductId)
    }

    @Test
    fun `we cannot create investment with not opened product`() {
        createInvestment(newInvestorId, notOpenedProductId, 10000)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_NOT_OPENED))

        checkInvestmentNotCreated(newInvestorId)
    }

    @Test
    fun `we cannot create investment with closed product`() {
        createInvestment(newInvestorId, closedProductId, 10000)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_CLOSED))

        checkInvestmentNotCreated(newInvestorId)
    }

    @Test
    fun `we cannot create investment with exceeded amount`() {
        createInvestment(newInvestorId, lastChanceProductId, 10000)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.TOTAL_INVESTING_AMOUNT_EXCEEDED))

        checkInvestmentNotCreated(newInvestorId)
        checkProductNotUpdated(lastChanceProductId)
    }

    @Test
    fun `we cannot create investment having same user id and same product id`() {
        createInvestment(newInvestorId, normalProductId, 10000)
            .andExpect(status().isCreated)

        createInvestment(newInvestorId, normalProductId, 10000)
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `we should handle multiple request for getting products`() {
        val latch = CountDownLatch(10)
        val executorService: ExecutorService = Executors.newFixedThreadPool(10)
        val counter = AtomicInteger(0)
        val futures: ArrayList<Future<Boolean>> = arrayListOf()

        for (i in 1..10) {
            futures.add(executorService.submit<Boolean> {
                var result = true
                val resultActions = getProducts()

                if (resultActions.andReturn().response.status != HttpStatus.OK.value()) {
                    result = false
                }

                counter.incrementAndGet()
                latch.countDown()

                return@submit result
            })
        }
        latch.await()

        executorService.shutdown()

        assert(counter.get() == 10)
        assert(latch.count == 0L)
        assert(futures.stream().filter { it.get() == false }.count() == 0L)
    }

    @Test
    fun `we should handle multiple request for getting investments`() {
        val latch = CountDownLatch(10)
        val executorService: ExecutorService = Executors.newFixedThreadPool(10)
        val counter = AtomicInteger(0)
        val futures: ArrayList<Future<Boolean>> = arrayListOf()

        for (i in 10..19) {
            futures.add(executorService.submit<Boolean> {
                var result = true
                val resultActions = getInvestments(i)

                if (resultActions.andReturn().response.status != HttpStatus.OK.value()) {
                    result = false
                }

                val content: String = resultActions.andReturn().response.contentAsString
                val investments: List<Investment> = objectMapper.readValue(content)

                for (investment in investments) {
                    if (investment.amount <= 0 || investment.userId != i) {
                        result = false
                    }
                }

                counter.incrementAndGet()
                latch.countDown()

                return@submit result
            })
        }
        latch.await()

        executorService.shutdown()

        assert(counter.get() == 10)
        assert(latch.count == 0L)
        assert(futures.stream().filter { it.get() == false }.count() == 0L)
    }

    @Test
    fun `we should handle multiple request for creating investments`() {
        val latch = CountDownLatch(10)
        val executorService: ExecutorService = Executors.newFixedThreadPool(10)
        val counter = AtomicInteger(0)
        val futures: ArrayList<Future<Boolean>> = arrayListOf()
        for (i in 20..29) {
            futures.add(executorService.submit<Boolean> {
                val result: Boolean
                val resultActions = createInvestment(i, i - 10, 100 + i)

                result = resultActions.andReturn().response.status == HttpStatus.CREATED.value()

                counter.incrementAndGet()
                latch.countDown()

                return@submit result
            })
        }
        latch.await()

        executorService.shutdown()

        assert(counter.get() == 10)
        assert(latch.count == 0L)
        assert(futures.stream().filter { it.get() == false }.count() == 0L)

        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<Product> = objectMapper.readValue(content)

        val updatedProducts: List<Product> = products.stream().filter { it.id in 10..19 }.toList()
        assert(updatedProducts.size == 10)
        for (i in 0..9) {
            val id = updatedProducts[i].id
            assert(updatedProducts[i].currentInvestingAmount == 110 + id)
            assert(updatedProducts[i].investorCount == 1)
            assert(updatedProducts[i].soldOut == 'N')
        }
    }

    @Test
    fun `we should handle multiple request for creating investments of same product`() {
        val latch = CountDownLatch(10)
        val executorService: ExecutorService = Executors.newFixedThreadPool(10)
        val counter = AtomicInteger(0)
        val futures: ArrayList<Future<Boolean>> = arrayListOf()

        for (i in 20..29) {
            futures.add(executorService.submit<Boolean> {
                val result: Boolean
                val resultActions: ResultActions = createInvestment(i, lastChanceProductId, 1)

                result = resultActions.andReturn().response.status == HttpStatus.CREATED.value()

                counter.incrementAndGet()
                latch.countDown()

                return@submit result
            })
        }
        latch.await()

        executorService.shutdown()

        assert(counter.get() == 10)
        assert(latch.count == 0L)
        assert(futures.stream().filter { it.get() == false }.count() == 9L)

        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<Product> = objectMapper.readValue(content)

        val updatedProducts: List<Product> = products.stream().filter { it.id == lastChanceProductId }.toList()
        assert(updatedProducts.size == 1)

        assert(updatedProducts[0].currentInvestingAmount == 2000000)
        assert(updatedProducts[0].investorCount == 3)
        assert(updatedProducts[0].soldOut == 'Y')
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

    private fun checkInvestmentNotCreated(userId: Int) {
        val resultActions: ResultActions = getInvestments(userId)
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val investments: List<Investment> = objectMapper.readValue(content)
        assert(investments.isEmpty())
    }

    private fun checkProductNotUpdated(productId: Int) {
        val resultActions: ResultActions = getProducts()
        resultActions.andExpect(status().isOk)

        val content: String = resultActions.andReturn().response.contentAsString
        val products: List<Product> = objectMapper.readValue(content)

        val checkProducts: List<Product> = products.stream().filter { it.id == productId }.toList()

        when (productId) {
            invalidProductId -> {
                assert(checkProducts.isEmpty())
            }
            lastChanceProductId -> {
                assert(checkProducts.size == 1)
                assert(checkProducts[0].currentInvestingAmount == 1999999)
                assert(checkProducts[0].investorCount == 2)
            }
            else -> {
                assert(checkProducts.size == 1)
                assert(checkProducts[0].currentInvestingAmount == 0)
                assert(checkProducts[0].investorCount == 0)
            }
        }
    }
}
package hey.jusang.invest

import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import hey.jusang.invest.repositories.InvestmentRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import java.time.LocalDateTime

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup(
    value = [
        Sql(
            scripts = ["/data/schema.sql", "/data/data.sql"],
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        Sql(
            scripts = ["/data/drop_db.sql"],
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
    ]
)
class InvestmentRepositoryTests {

    @Autowired
    lateinit var testRepository: InvestmentRepository

    @Test
    fun `testRepository should be configured`() {
    }

    @Test
    fun `we should get 23 products (including 1 sold out) when requesting a list of product within the period`() {
        val current: LocalDateTime = LocalDateTime.now()
        var soldOut = 0
        val products: List<Product> = testRepository.selectProducts()
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
        val investments: List<Investment> = testRepository.selectInvestments(10)
        assert(investments.size == 2)

        for (investment in investments) {
            assert(investment.amount > 0)
            assert(investment.userId == 10)
        }
    }

    @Test
    fun `we should get product with an ID of 1`() {
        val product: Product? = testRepository.selectProductForUpdate(1)
        assert(product?.id == 1)
    }

    @Test
    fun `we cannot get product without proper ID`() {
        val product: Product? = testRepository.selectProductForUpdate(9999)
        assert(product == null)
    }

    @Test
    fun `we should update product with an ID of 1`() {
        val count: Int = testRepository.updateProduct(100, 1)
        assert(count == 1)

        val product: Product? = testRepository.selectProductForUpdate(1)
        assert(product?.id == 1)
        assert(product?.currentInvestingAmount == 100)
        assert(product?.investorCount == 1)
    }

    @Test
    fun `we cannot update product over total investing amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            testRepository.updateProduct(100000000, 1)
        }
    }

    @Test
    fun `we should create investment`() {
        val success: Int = testRepository.insertInvestment(1, 100, 1)
        assert(success == 1)

        val investments: List<Investment> = testRepository.selectInvestments(1)
        assert(investments.size == 1)
        assert(investments[0].userId == 1)
        assert(investments[0].productId == 1)
        assert(investments[0].amount == 100)
    }

    @Test
    fun `we cannot create investment having same user id and same product id`() {
        val success: Int = testRepository.insertInvestment(1, 100, 1)
        assert(success == 1)

        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            testRepository.insertInvestment(1, 100, 1)
        }
    }

    @Test
    fun `we cannot create investment having negative amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            testRepository.insertInvestment(1, -100, 1)
        }
    }

    @Test
    fun `we cannot create investment having zero amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            testRepository.insertInvestment(1, 0, 1)
        }
    }
}
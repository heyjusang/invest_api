package hey.jusang.invest

import hey.jusang.invest.entities.Investment
import hey.jusang.invest.repositories.InvestmentRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class InvestmentRepositoryTests {
    @Autowired
    lateinit var testRepository: InvestmentRepository

    @Test
    fun `testRepository should be configured`() {
    }

    @Test
    fun `we should get 2 investments when requesting a list of investment for user with an ID of 2`() {
        val investments: List<Investment> = testRepository.findAllByUserId(2)
        assert(investments.size == 2)

        for (investment in investments) {
            assert(investment.amount > 0)
            assert(investment.userId == 2L)
        }
    }

    @Test
    fun `we should create investment`() {
        val investment = Investment(1, 1, 100)

        testRepository.save(investment)

        val investments: List<Investment> = testRepository.findAllByUserId(1)
        assert(investments.size == 1)
        assert(investments[0].userId == 1L)
        assert(investments[0].productId == 1L)
        assert(investments[0].amount == 100)
    }

    @Test
    fun `we cannot create investment having same user id and same product id`() {
        val investment = Investment(1, 1, 100)

        testRepository.save(investment)

        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            val investment2 = Investment(1, 1, 100)

            testRepository.save(investment2)
        }
    }

    @Test
    fun `we cannot create investment having negative amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            val investment = Investment(1, 1, -100)

            testRepository.save(investment)
        }
    }

    @Test
    fun `we cannot create investment having zero amount`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            val investment = Investment(1, 1, 0)

            testRepository.save(investment)
        }
    }
}
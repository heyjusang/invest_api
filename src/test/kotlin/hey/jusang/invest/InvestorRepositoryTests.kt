package hey.jusang.invest

import hey.jusang.invest.entities.Investor
import hey.jusang.invest.repositories.InvestorRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class InvestorRepositoryTests {
    @Autowired
    lateinit var testRepository: InvestorRepository

    @Test
    fun `testRepository should be configured`() {
    }

    @Test
    fun `we should get 1 investor when requesting investor whose name is hey`() {
        val investor: Investor = testRepository.findByName("user1").get()

        assert(investor.name == "user1")
    }

    @Test
    fun `we cannot get investor without proper name`() {
        val empty: Boolean = testRepository.findByName("no name").isEmpty

        assert(empty)
    }

    @Test
    fun `we should get 1 when requesting count of investor whose name is hey`() {
        val count: Long = testRepository.countByName("user1")

        assert(count == 1L)
    }

    @Test
    fun `we should create investor`() {
        val investor = Investor("heyhey", "jusang", "USER")

        val updated: Investor = testRepository.save(investor)

        assert(updated.name == "heyhey")
        assert(updated.password == "jusang")
        assert(updated.role == "USER")
    }
}
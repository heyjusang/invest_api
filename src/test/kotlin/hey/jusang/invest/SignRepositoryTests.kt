package hey.jusang.invest

import hey.jusang.invest.models.User
import hey.jusang.invest.repositories.SignRepository
import org.junit.jupiter.api.Test
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
class SignRepositoryTests {

    @Autowired
    lateinit var testRepository: SignRepository

    @Test
    fun `testRepository should be configured`() {
    }

    @Test
    fun `we should get 1 investor when requesting investor whose name is hey`() {
        val user: User? = testRepository.selectUserByName("hey")

        assert(user != null)
        assert(user?.name == "hey")
    }

    @Test
    fun `we cannot get investor without proper name`() {
        val user: User? = testRepository.selectUserByName("no name")

        assert(user == null)
    }

    @Test
    fun `we should get 1 when requesting count of investor whose name is hey`() {
        val count: Int = testRepository.selectUserCountByName("hey")

        assert(count == 1)
    }

    @Test
    fun `we should create investor`() {
        val success: Int = testRepository.insertUser("heyhey", "jusang")

        assert(success == 1)
    }
}
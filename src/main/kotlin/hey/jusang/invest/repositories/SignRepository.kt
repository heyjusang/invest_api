package hey.jusang.invest.repositories

import hey.jusang.invest.models.User
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface SignRepository {

    @Select(
        "SELECT * FROM investor WHERE name = #{name}"
    )
    fun selectUserByName(name: String): User?

    @Select(
        "SELECT COUNT(*) FROM investor WHERE name = #{name}"
    )
    fun selectUserCountByName(name: String): Int

    @Insert(
        "INSERT INTO investor(name, password) VALUES (#{name}, #{password})"
    )
    fun insertUser(name: String, password: String): Int
}
package hey.jusang.invest.repositories

import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

@Mapper
interface InvestmentRepository {

    @Select(
        "SELECT id, title, total_investing_amount, current_investing_amount, investor_count,\n" +
        "   started_at, finished_at,\n" +
        "   CASE WHEN total_investing_amount > current_investing_amount THEN 'N'\n" +
        "       ELSE 'Y'\n" +
        "   END AS sold_out\n" +
        "FROM product\n" +
        "WHERE SYSDATE >= started_at AND SYSDATE < finished_at"
    )
    fun selectProducts(): List<Product>

    @Select(
        "SELECT i.id, i.user_id, i.product_id, p.title AS product_title, p.total_investing_amount, i.amount, i.created_at\n" +
        "FROM investment i, product p\n" +
        "WHERE i.user_id = #{userId} AND p.id = i.product_id"
    )
    fun selectInvestments(userId: Int): List<Investment>

    @Select(
        "SELECT *\n" +
        "FROM product WHERE id = #{productId} FOR UPDATE")
    fun selectProductForUpdate(productId: Int): Product?

    @Update(
        "UPDATE product\n" +
        "SET current_investing_amount = current_investing_amount + #{amount},\n" +
        "    investor_count = investor_count + 1\n" +
        "WHERE id = #{productId}")
    fun updateProduct(amount: Int, productId: Int): Int


    @Insert(
        "INSERT INTO investment(user_id, amount, product_id)\n" +
        "VALUES (#{userId}, #{amount}, #{productId})")
    fun insertInvestment(userId: Int, amount: Int, productId: Int): Int
}
package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.BaseException
import hey.jusang.invest.exceptions.ErrorMessage
import hey.jusang.invest.exceptions.ForbiddenRequestException
import hey.jusang.invest.models.Investment
import hey.jusang.invest.models.Product
import hey.jusang.invest.services.InvestmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.sql.SQLException

@RestController
class InvestmentController(val investmentService: InvestmentService) {
    @GetMapping("/products")
    fun getProducts(): ResponseEntity<List<Product>> {
        return ResponseEntity(investmentService.getProducts(), HttpStatus.OK)
    }

    @GetMapping("/investments")
    fun getInvestments(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Int
    ): ResponseEntity<List<Investment>> {
        checkAuthId(authentication, userId)

        return ResponseEntity(investmentService.getInvestments(userId), HttpStatus.OK)
    }

    @PostMapping("/investment")
    fun createInvestment(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Int,
        @RequestParam("product_id") productId: Int,
        @RequestParam("amount") amount: Int
    ): ResponseEntity<Map<String, Boolean>> {
        checkAuthId(authentication, userId)

        val success: Boolean = investmentService.createInvestment(userId, productId, amount)
        return ResponseEntity(mapOf("success" to success), HttpStatus.CREATED)
    }

    @ExceptionHandler(SQLException::class)
    fun sqlException(e: SQLException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(BaseException::class)
    fun baseException(e: BaseException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), e.statusCode)
    }

    private fun checkAuthId(authentication: Authentication, userId: Int) {
        val details: UserDetails = authentication.principal as UserDetails
        val authId: String = details.username

        if (authId != userId.toString()) throw ForbiddenRequestException()
    }
}
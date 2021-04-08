package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.BaseException
import hey.jusang.invest.exceptions.ErrorMessage
import hey.jusang.invest.exceptions.ForbiddenRequestException
import hey.jusang.invest.entities.Investment
import hey.jusang.invest.entities.Product
import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.services.InvestmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.sql.SQLException
import java.time.LocalDateTime

@RestController
class InvestmentController(val investmentService: InvestmentService) {
    @GetMapping("/products")
    fun getProducts(): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity(investmentService.getProducts(LocalDateTime.now()), HttpStatus.OK)
    }

    @GetMapping("/investments")
    fun getInvestments(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long
    ): ResponseEntity<List<InvestmentDTO>> {
        checkAuthId(authentication, userId)

        return ResponseEntity(investmentService.getInvestments(userId), HttpStatus.OK)
    }

    @PostMapping("/investment")
    fun createInvestment(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam("product_id") productId: Long,
        @RequestParam("amount") amount: Int
    ): ResponseEntity<InvestmentDTO> {
        checkAuthId(authentication, userId)

        return ResponseEntity(investmentService.createInvestment(userId, productId, amount), HttpStatus.CREATED)
    }

    @ExceptionHandler(SQLException::class)
    fun sqlException(e: SQLException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(BaseException::class)
    fun baseException(e: BaseException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), e.statusCode)
    }

    private fun checkAuthId(authentication: Authentication, userId: Long) {
        val details: UserDetails = authentication.principal as UserDetails
        val authId: String = details.username

        if (authId != userId.toString()) throw ForbiddenRequestException()
    }
}
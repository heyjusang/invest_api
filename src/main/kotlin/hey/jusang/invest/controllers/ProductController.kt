package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.BaseException
import hey.jusang.invest.exceptions.ErrorMessage
import hey.jusang.invest.exceptions.ForbiddenRequestException
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.services.ProductService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.sql.SQLException
import java.time.LocalDateTime

@RestController
class ProductController(val productService: ProductService) {
    @GetMapping("/products")
    fun getProducts(): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity(productService.getProducts(), HttpStatus.OK)
    }

    @PostMapping("/product")
    fun createProduct(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam("title") title: String,
        @RequestParam("total_investing_amount") totalInvestingAmount: Int,
        @RequestParam("started_at") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startedAt: LocalDateTime,
        @RequestParam("finished_at") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) finishedAt: LocalDateTime
    ): ResponseEntity<ProductDTO> {
        // TODO: DateTimeFormatConfiguration
        // TODO: change RequestParam to ProductDTO
        checkAuthId(authentication, userId)

        return ResponseEntity(
            productService.createProduct(title, totalInvestingAmount, startedAt, finishedAt),
            HttpStatus.CREATED
        )
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
        // TODO: Role
        val details: UserDetails = authentication.principal as UserDetails
        val authId: String = details.username

        if (authId != userId.toString()) throw ForbiddenRequestException()
    }
}
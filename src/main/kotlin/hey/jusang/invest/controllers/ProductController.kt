package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.ForbiddenRequestException
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.services.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class ProductController(val productService: ProductService) {
    @GetMapping("/products")
    fun getProducts(): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity(productService.getProducts(), HttpStatus.OK)
    }

    @PostMapping("/product")
    fun createInvestment(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam("title") title: String,
        @RequestParam("total_investing_amount") totalInvestingAmount: Int,
        @RequestParam("started_at") startedAt: LocalDateTime,
        @RequestParam("finished_at") finishedAt: LocalDateTime
    ): ResponseEntity<ProductDTO> {
        // TODO: change RequestParam to ProductDTO
        checkAuthId(authentication, userId)

        return ResponseEntity(
            productService.createProduct(title, totalInvestingAmount, startedAt, finishedAt),
            HttpStatus.CREATED
        )
    }

    // TODO Exception

    private fun checkAuthId(authentication: Authentication, userId: Long) {
        // TODO: Role
        val details: UserDetails = authentication.principal as UserDetails
        val authId: String = details.username

        if (authId != userId.toString()) throw ForbiddenRequestException()
    }
}
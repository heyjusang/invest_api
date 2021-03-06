package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.ForbiddenRequestException
import hey.jusang.invest.models.ProductDTO
import hey.jusang.invest.services.ProductService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
class ProductController(val productService: ProductService) {
    @GetMapping("/products")
    fun getProducts(@RequestParam page: Int, @RequestParam size: Int): ResponseEntity<Slice<ProductDTO.Response>> {
        return ResponseEntity(productService.getProducts(PageRequest.of(page, size)), HttpStatus.OK)
    }

    @PostMapping("/product")
    fun createProduct(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long,
        @ModelAttribute product: ProductDTO.Request
    ): ResponseEntity<ProductDTO.Response> {
        checkAuthId(authentication, userId)

        return ResponseEntity(
            productService.createProduct(product),
            HttpStatus.CREATED
        )
    }

    private fun checkAuthId(authentication: Authentication, userId: Long) {
        // TODO: Role
        val details: UserDetails = authentication.principal as UserDetails
        val authId: String = details.username

        if (authId != userId.toString()) throw ForbiddenRequestException()
    }
}
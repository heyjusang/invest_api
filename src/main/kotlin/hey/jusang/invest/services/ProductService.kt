package hey.jusang.invest.services

import hey.jusang.invest.models.ProductDTO
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ProductService {
    fun getProducts(pageable: Pageable): Slice<ProductDTO.Response>
    fun createProduct(productDTO: ProductDTO.Request): ProductDTO.Response
}
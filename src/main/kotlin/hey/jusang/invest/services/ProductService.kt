package hey.jusang.invest.services

import hey.jusang.invest.models.ProductDTO

interface ProductService {
    fun getProducts(): List<ProductDTO.Response>
    fun createProduct(productDTO: ProductDTO.Request): ProductDTO.Response
}
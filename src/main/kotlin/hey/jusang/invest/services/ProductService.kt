package hey.jusang.invest.services

import hey.jusang.invest.models.CreateProductDTO
import hey.jusang.invest.models.ResponseProductDTO

interface ProductService {
    fun getProducts(): List<ResponseProductDTO>
    fun createProduct(productDTO: CreateProductDTO): ResponseProductDTO
}
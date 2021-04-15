package hey.jusang.invest.services

import hey.jusang.invest.models.InvestorDTO

interface SignService {
    fun signIn(name: String, password: String): String
    fun signUp(name: String, password: String): InvestorDTO
}
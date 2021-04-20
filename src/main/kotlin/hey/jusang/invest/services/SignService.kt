package hey.jusang.invest.services

import hey.jusang.invest.models.InvestorDTO

interface SignService {
    fun signIn(investorDTO: InvestorDTO.Request): String
    fun signUp(investorDTO: InvestorDTO.Request): InvestorDTO.Response
}
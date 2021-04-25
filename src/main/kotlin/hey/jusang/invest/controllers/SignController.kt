package hey.jusang.invest.controllers

import hey.jusang.invest.models.InvestorDTO
import hey.jusang.invest.services.SignService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SignController(val signService: SignService) {
    @PostMapping("/signin")
    fun signIn(@ModelAttribute investor: InvestorDTO.Request): ResponseEntity<Map<String, String>> {
        val token: String = signService.signIn(investor)
        return ResponseEntity(mapOf("token" to token), HttpStatus.OK)
    }

    @PostMapping("/signup")
    fun signUp(@ModelAttribute investor: InvestorDTO.Request): ResponseEntity<InvestorDTO.Response> {
        return ResponseEntity(signService.signUp(investor), HttpStatus.CREATED)
    }
}
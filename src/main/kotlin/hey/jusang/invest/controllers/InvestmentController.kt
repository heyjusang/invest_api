package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.ForbiddenRequestException
import hey.jusang.invest.models.InvestmentDTO
import hey.jusang.invest.services.InvestmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
class InvestmentController(val investmentService: InvestmentService) {
    @GetMapping("/investments")
    fun getInvestments(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long
    ): ResponseEntity<List<InvestmentDTO.Response>> {
        checkAuthId(authentication, userId)

        return ResponseEntity(investmentService.getInvestments(userId), HttpStatus.OK)
    }

    @PostMapping("/investment")
    fun createInvestment(
        authentication: Authentication,
        @RequestHeader("X-USER-ID") userId: Long,
        @ModelAttribute investment: InvestmentDTO.Request
    ): ResponseEntity<InvestmentDTO.Response> {
        checkAuthId(authentication, userId)

        return ResponseEntity(investmentService.createInvestment(userId, investment), HttpStatus.CREATED)
    }

    private fun checkAuthId(authentication: Authentication, userId: Long) {
        val details: UserDetails = authentication.principal as UserDetails
        val authId: String = details.username

        if (authId != userId.toString()) throw ForbiddenRequestException()
    }
}
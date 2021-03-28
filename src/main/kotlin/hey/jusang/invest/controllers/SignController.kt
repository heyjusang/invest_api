package hey.jusang.invest.controllers

import hey.jusang.invest.exceptions.BaseException
import hey.jusang.invest.exceptions.ErrorMessage
import hey.jusang.invest.services.SignService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.SQLException

@RestController
class SignController(val signService: SignService) {

    @PostMapping("/signin")
    fun signIn(
        @RequestParam("name", required = true) name: String,
        @RequestParam("password", required = true) password: String
    ): ResponseEntity<Map<String, String>> {
        val token: String = signService.signIn(name, password)
        return ResponseEntity(mapOf("token" to token), HttpStatus.OK)
    }

    @PostMapping("/signup")
    fun signUp(
        @RequestParam("name", required = true) name: String,
        @RequestParam("password", required = true) password: String
    ): ResponseEntity<Map<String, Boolean>> {
        val success: Boolean = signService.signUp(name, password)
        return ResponseEntity(mapOf("success" to success), HttpStatus.CREATED)
    }

    @ExceptionHandler(SQLException::class)
    fun sqlException(e: SQLException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(BaseException::class)
    fun baseException(e: BaseException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), e.statusCode)
    }
}
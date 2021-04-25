package hey.jusang.invest.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.sql.SQLException

@RestControllerAdvice
class ExceptionControllerAdvice {
    @ExceptionHandler(SQLException::class)
    fun sqlException(e: SQLException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(BaseException::class)
    fun baseException(e: BaseException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.errorCode, e.message), e.statusCode)
    }

    @ExceptionHandler(Exception::class)
    fun exception(e: Exception): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(ErrorCode.INTERNAL_ERROR, e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
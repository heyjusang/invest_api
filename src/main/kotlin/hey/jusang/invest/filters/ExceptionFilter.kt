package hey.jusang.invest.filters

import com.fasterxml.jackson.databind.ObjectMapper
import hey.jusang.invest.exceptions.BaseException
import hey.jusang.invest.exceptions.ErrorCode
import hey.jusang.invest.exceptions.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ExceptionFilter(val objectMapper: ObjectMapper) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: BaseException) {
            handleErrorResponse(response, ResponseEntity(ErrorMessage(e.errorCode, e.message), e.statusCode))
        } catch (e: Exception) {
            handleErrorResponse(
                response,
                ResponseEntity(ErrorMessage(ErrorCode.INTERNAL_ERROR, e.message), HttpStatus.INTERNAL_SERVER_ERROR)
            )
        }
    }

    private fun handleErrorResponse(response: HttpServletResponse, responseEntity: ResponseEntity<ErrorMessage>) {
        response.status = responseEntity.statusCodeValue
        response.contentType = "application/json"
        response.writer.write(objectMapper.writeValueAsString(responseEntity.body))
    }
}
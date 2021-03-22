package hey.jusang.invest.filters

import hey.jusang.invest.utils.JwtTokenProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class JwtAuthenticationFilter(val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, filterChain: FilterChain?) {
        val token: String? = jwtTokenProvider.resolveToken(request as HttpServletRequest)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(token)
        }

        filterChain?.doFilter(request, response)
    }
}
package hey.jusang.invest.utils

import hey.jusang.invest.exceptions.InvalidAuthInformationException
import hey.jusang.invest.models.InvestorDTO
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(val investUserDetailsService: InvestUserDetailsService) {
    @Value("\${spring.jwt.secret}")
    private lateinit var secret: String
    private lateinit var key: Key
    private val expiration: Long = 1000 * 60 * 60

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun createToken(investor: InvestorDTO.Data): String {
        val now = Date()
        val claims: Claims = Jwts.claims().setSubject(investor.id.toString())
        claims["password"] = investor.encryptedPassword
        claims["role"] = investor.role

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expiration))
            .signWith(key)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        val id: String = claims.subject
        val userDetails: UserDetails = investUserDetailsService.loadUserByUsername(id)

        validateTokenDetails(claims, userDetails)

        return UsernamePasswordAuthenticationToken(userDetails, userDetails.password, userDetails.authorities)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("X-AUTH-TOKEN")
    }

    fun validateTokenExpiration(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)

            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun validateTokenDetails(claims: Claims, user: UserDetails): Boolean {
        if (claims["password"] != user.password) {
            throw InvalidAuthInformationException()
        }

        if (!user.authorities.any { it.authority == "ROLE_${claims["role"]}" }) {
            throw InvalidAuthInformationException()
        }

        return true
    }
}
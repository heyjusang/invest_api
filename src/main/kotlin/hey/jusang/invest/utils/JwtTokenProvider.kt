package hey.jusang.invest.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.lang.Exception
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider {
    @Value("spring.jwt.secret")
    private lateinit var secretKey: String
    private val expiration: Long = 1000 * 60 * 60

    @PostConstruct
    fun init() {
        Base64.getEncoder().encodeToString(secretKey.toByteArray()).also { secretKey = it }
    }

    fun createToken(userId: Int, roles: List<String>): String {
        val now: Date = Date()
        val claims: Claims = Jwts.claims().setSubject(userId.toString())
        claims["roles"] = roles

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expiration))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        // TODO
        return UsernamePasswordAuthenticationToken("", "")
    }

    fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("X-AUTH-TOKEN")
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)

            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}
package hey.jusang.invest.utils

import hey.jusang.invest.models.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.lang.Exception
import java.security.Key
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

    fun createToken(user: User): String {
        val now = Date()
        val claims: Claims = Jwts.claims().setSubject(user.id.toString())
        val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        claims["name"] = user.name
        claims["password"] = user.password
        claims["role"] = user.role

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expiration))
            .signWith(key)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body
        val grantedAuthority: GrantedAuthority = SimpleGrantedAuthority(claims["role"] as String?)

        return UsernamePasswordAuthenticationToken(claims["name"], claims["password"], listOf(grantedAuthority))
    }

    fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("X-AUTH-TOKEN")
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)

            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}
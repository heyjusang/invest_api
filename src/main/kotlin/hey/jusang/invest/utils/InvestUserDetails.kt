package hey.jusang.invest.utils

import hey.jusang.invest.entities.Investor
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class InvestUserDetails(
    private val id: String,
    private val password: String
) : UserDetails {
    private val authorities: MutableCollection<GrantedAuthority> = mutableListOf()

    constructor(investor: Investor) : this(investor.id.toString(), investor.password) {
        // TODO: enum ? collection ?
        if (investor.role == "ADMIN") {
            authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        }
        authorities.add(SimpleGrantedAuthority("ROLE_${investor.role}"))
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = password
    override fun getUsername(): String = id

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
package hey.jusang.invest.configs

import hey.jusang.invest.filters.ExceptionFilter
import hey.jusang.invest.filters.JwtAuthenticationFilter
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class SecurityConfiguration(val jwtAuthenticationFilter: JwtAuthenticationFilter, val exceptionFilter: ExceptionFilter) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests().antMatchers("/signin", "/signup").permitAll()
            .antMatchers(HttpMethod.GET, "/products").permitAll()
            .antMatchers(HttpMethod.POST, "/product").hasRole("ADMIN")
            .anyRequest().hasRole("USER")
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(exceptionFilter, JwtAuthenticationFilter::class.java)
    }
}
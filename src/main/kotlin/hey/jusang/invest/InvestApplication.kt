package hey.jusang.invest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock

@SpringBootApplication
@EnableAspectJAutoProxy
class InvestApplication {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}

fun main(args: Array<String>) {
    runApplication<InvestApplication>(*args)
}

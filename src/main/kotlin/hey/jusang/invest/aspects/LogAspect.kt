package hey.jusang.invest.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Aspect
@ConditionalOnExpression("\${aspect.enabled}")
class LogAspect {
    @Before("@annotation(hey.jusang.invest.annotations.LogExecutionTime)")
    fun beforeExecutingTime(jointPoint: JoinPoint) {
        println("${LocalDateTime.now()} [START] ${jointPoint.signature}")
    }

    @After("@annotation(hey.jusang.invest.annotations.LogExecutionTime)")
    fun afterExecutingTime(jointPoint: JoinPoint) {
        println("${LocalDateTime.now()} [END] ${jointPoint.signature}")
    }
}
package hey.jusang.invest.aspects

import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
@Aspect
@ConditionalOnExpression("\${aspect.enabled}")
class LogAspect {
    @Before("@annotation(hey.jusang.invest.annotations.LogExecutionTime)")
    fun beforeExecutingTime(jointPoint: JoinPoint) {
        logger.info { "${LocalDateTime.now()} [START] ${jointPoint.signature}" }
    }

    @After("@annotation(hey.jusang.invest.annotations.LogExecutionTime)")
    fun afterExecutingTime(jointPoint: JoinPoint) {
        logger.info { "${LocalDateTime.now()} [END] ${jointPoint.signature}" }
    }
}
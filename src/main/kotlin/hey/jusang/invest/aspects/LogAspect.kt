package hey.jusang.invest.aspects

import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
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

    @AfterThrowing(throwing="throwable", pointcut="within(hey.jusang.invest.services.*)")
    fun afterException(jointPoint: JoinPoint, throwable: Throwable) {
        logger.error(throwable) { "${LocalDateTime.now()} [THROW] ${jointPoint.signature}: $throwable" }
    }
}
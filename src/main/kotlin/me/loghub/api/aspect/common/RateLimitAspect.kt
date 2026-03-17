package me.loghub.api.aspect.common

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.constant.message.ServerMessage
import me.loghub.api.entity.user.User
import me.loghub.api.exception.common.TooManyRequestsException
import me.loghub.api.lib.ratelimit.RateLimit
import me.loghub.api.service.common.RateLimitService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class RateLimitAspect(private val rateLimitService: RateLimitService) {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @Before("@annotation(rateLimit)")
    fun checkRateLimit(joinPoint: JoinPoint, rateLimit: RateLimit) {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: error(ServerMessage.AUTHENTICATION_NOT_FOUND)

        val principal = authentication.principal as? User
            ?: error(ServerMessage.PRINCIPAL_NOT_USER)

        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method

        val allowed = rateLimitService.tryConsume(
            userId = principal.persistedId,
            className = method.declaringClass.name,
            methodName = method.name,
            limit = rateLimit.limit,
            window = rateLimit.window,
            unit = rateLimit.unit
        )

        if (!allowed) {
            logger.info { "[RateLimit] too many requests: { userId=${principal.id}, point=${method.declaringClass.name}.${method.name} }" }
            throw TooManyRequestsException()
        }
    }
}
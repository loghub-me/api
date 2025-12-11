package me.loghub.api.aspect.common

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
    @Before("@annotation(rateLimit)")
    fun checkRateLimit(joinPoint: JoinPoint, rateLimit: RateLimit) {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication found")

        val principal = authentication.principal as? User
            ?: throw IllegalStateException("Principal is not User")

        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method

        val allowed = rateLimitService.tryConsume(
            userId = principal.id!!,
            className = method.declaringClass.name,
            methodName = method.name,
            limit = rateLimit.limit,
            window = rateLimit.window,
            unit = rateLimit.unit
        )

        if (!allowed) {
            throw TooManyRequestsException()
        }
    }
}
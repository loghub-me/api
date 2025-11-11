package me.loghub.api.aspect.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.dto.auth.join.JoinConfirmDTO
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class JoinAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(pointcut = "execution(* me.loghub.api.service.auth.JoinService.confirmJoin(..)) && args(requestBody)")
    fun afterJoinConfirm(requestBody: JoinConfirmDTO) =
        logger.info { "[Join] confirmed: { email=\"${requestBody.email}\" }" }
}
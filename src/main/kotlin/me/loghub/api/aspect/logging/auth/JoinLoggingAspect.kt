package me.loghub.api.aspect.logging.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.dto.auth.JoinConfirmDTO
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class JoinLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(pointcut = "execution(* me.loghub.api.service.auth.JoinService.confirmJoin(..)) && args(requestBody)")
    fun afterJoinConfirm(requestBody: JoinConfirmDTO) =
        logger.info { "[Join] confirmed: { email=\"${requestBody.email}\" }" }
}
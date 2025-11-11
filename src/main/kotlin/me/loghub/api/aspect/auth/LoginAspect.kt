package me.loghub.api.aspect.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.dto.auth.login.LoginConfirmDTO
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LoginAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(pointcut = "execution(* me.loghub.api.service.auth.LoginService.confirmLogin(..)) && args(requestBody)")
    fun afterLoginConfirm(requestBody: LoginConfirmDTO) =
        logger.info { "[Login] confirmed: { email=\"${requestBody.email}\" }" }
}
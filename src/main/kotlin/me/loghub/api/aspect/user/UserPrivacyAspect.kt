package me.loghub.api.aspect.user

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UserPrivacyAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserPrivacyService.updatePrivacy(..)) && args(.., user)",
    )
    fun afterUpdatePrivacy(user: User) =
        logger.info { "[UserPrivacy] update privacy: { userId=${user.id} }" }
}
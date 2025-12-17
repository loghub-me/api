package me.loghub.api.aspect.user

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UserProfileAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserProfileService.updateProfile(..)) && args(.., user)",
    )
    fun afterUpdateProfile(user: User) =
        logger.info { "[UserProfile] update profile: { userId=${user.id} }" }
}

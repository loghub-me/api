package me.loghub.api.aspect.user

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.dto.user.UpdateUsernameDTO
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UserAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserService.updateUsername(..)) && args(requestBody, user)",
    )
    fun afterUpdateUsername(requestBody: UpdateUsernameDTO, user: User) =
        logger.info { "[User] update username: { userId=${user.id}, oldUsername=\"${user.username}\", newUsername=\"${requestBody.newUsername}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserService.updateAvatar(..)) && args(.., user)",
    )
    fun afterUpdateAvatar(user: User) =
        logger.info { "[User] update avatar: { userId=${user.id} }" }
}
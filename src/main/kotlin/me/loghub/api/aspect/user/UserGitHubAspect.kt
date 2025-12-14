package me.loghub.api.aspect.user

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.dto.user.UpdateUserGitHubDTO
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UserGitHubAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserGitHubService.updateGitHub(..)) && args(requestBody, user)",
    )
    fun afterUpdatePrivacy(requestBody: UpdateUserGitHubDTO, user: User) =
        logger.info { "[User] update privacy: { userId=${user.id}, githubUsername=${requestBody.username} }" }
}
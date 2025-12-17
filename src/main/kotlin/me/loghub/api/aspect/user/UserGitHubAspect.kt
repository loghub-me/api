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
        logger.info { "[UserGitHub] update github: { userId=${user.id}, githubUsername=${requestBody.username} }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserGitHubService.deleteGitHub(..)) && args(user)",
    )
    fun afterDeletePrivacy(user: User) =
        logger.info { "[UserGitHub] delete github: { userId=${user.id} }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserGitHubService.verifyGitHub(..)) && args(user)",
    )
    fun afterVerifyGitHub(user: User) =
        logger.info { "[UserGitHub] verify github: { userId=${user.id}, githubUsername=${user.github.username} }" }
}
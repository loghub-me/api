package me.loghub.api.aspect.user

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UserImageAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.user.UserImageService.uploadImage(..)) && args(.., user)",
        returning = "uploadedPath"
    )
    fun afterUploadImage(user: User, uploadedPath: String) =
        logger.info { "[UserImage] uploaded: { userId=${user.id}, uploadedPath=\"${uploadedPath}\" }" }
}
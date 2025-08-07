package me.loghub.api.service.auth

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserPrivacy
import me.loghub.api.entity.user.UserProfile
import me.loghub.api.repository.user.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService(private val userRepository: UserRepository) : DefaultOAuth2UserService() {
    private object RegistrationId {
        const val GOOGLE = "google"
        const val GITHUB = "github"
    }

    private object OAuth2Attribute {
        const val EMAIL = "email"
        const val USERNAME = "username"
        const val PROVIDER = "provider"
    }

    @Transactional
    override fun loadUser(request: OAuth2UserRequest): OAuth2User {
        val registrationId = request.clientRegistration.registrationId;
        val user = super.loadUser(request)
        val attributes = mapAttributes(registrationId, user.attributes)
        return DefaultOAuth2User(user.authorities, attributes, "email")
    }

    private fun mapAttributes(registrationId: String, attributes: Map<String, Any>) = when (registrationId) {
        RegistrationId.GOOGLE -> mapOf(
            OAuth2Attribute.EMAIL to attributes["email"].toString(),
            OAuth2Attribute.USERNAME to attributes["name"].toString(),
            OAuth2Attribute.PROVIDER to User.Provider.GOOGLE,
        )

        RegistrationId.GITHUB -> mapOf(
            OAuth2Attribute.EMAIL to attributes["email"].toString(),
            OAuth2Attribute.USERNAME to attributes["login"].toString(),
            OAuth2Attribute.PROVIDER to User.Provider.GITHUB,
        )

        else -> throw UnsupportedOperationException(ResponseMessage.Auth.UNSUPPORTED_OAUTH2_PROVIDER)
    }

    fun findOrCreateUser(authentication: Authentication): User {
        val oAuth2User = authentication.principal as OAuth2User
        return userRepository.findByUsername(oAuth2User.name)
            ?: createUser(oAuth2User)
    }

    private fun createUser(oAuth2User: OAuth2User) = userRepository.save(
        User(
            email = oAuth2User.attributes[OAuth2Attribute.EMAIL].toString(),
            username = oAuth2User.attributes[OAuth2Attribute.USERNAME].toString(),
            provider = oAuth2User.attributes[OAuth2Attribute.PROVIDER] as User.Provider,
            profile = UserProfile(nickname = oAuth2User.attributes["username"].toString()),
            privacy = UserPrivacy(),
        )
    )
}
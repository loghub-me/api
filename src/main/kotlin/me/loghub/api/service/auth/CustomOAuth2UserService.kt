package me.loghub.api.service.auth

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.oauth2.OAuth2UserAttributes
import me.loghub.api.entity.user.User
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {
    @Transactional
    override fun loadUser(request: OAuth2UserRequest): OAuth2User {
        val registrationId = request.clientRegistration.registrationId;
        val user = super.loadUser(request)
        val attributes = extractAttributes(registrationId, user.attributes)
        return DefaultOAuth2User(user.authorities, attributes.toMap(), OAuth2UserAttributes::email.name)
    }

    private fun extractAttributes(registrationId: String, attributes: Map<String, Any>) = when (registrationId) {
        User.Provider.GOOGLE.registrationId -> OAuth2UserAttributes(
            attributes["email"].toString(),
            User.Provider.GOOGLE,
        )

        User.Provider.GITHUB.registrationId -> OAuth2UserAttributes(
            attributes["email"].toString(),
            User.Provider.GITHUB,
        )

        else -> throw UnsupportedOperationException(ResponseMessage.Auth.UNSUPPORTED_OAUTH2_PROVIDER)
    }
}
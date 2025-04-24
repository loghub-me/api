package kr.loghub.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kr.loghub.api.constant.ServerMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig {
    @Bean
    fun jwtAlgorithm(@Value("\${access-token.secret}") secret: String) =
        Algorithm.HMAC512(secret)
            ?: throw IllegalStateException(ServerMessage.FAILED_BUILD_JWT_ALGORITHM)

    @Bean
    fun jwtVerifier(@Value("\${access-token.issuer}") issuer: String, algorithm: Algorithm) =
        JWT.require(algorithm).withIssuer(issuer).build()
            ?: throw IllegalStateException(ServerMessage.FAILED_BUILD_JWT_VERIFIER)
}
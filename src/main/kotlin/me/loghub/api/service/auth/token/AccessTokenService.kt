package me.loghub.api.service.auth.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.token.AccessToken
import me.loghub.api.entity.user.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class AccessTokenService(
    @Value("\${jwt.issuer}") private val issuer: String,
    @Value("\${jwt.expiration}") private val expiration: Long,
    private val jwtAlgorithm: Algorithm,
    private val jwtVerifier: JWTVerifier,
) {
    object JwtClaims {
        const val EMAIL = "email"
        const val USERNAME = "username"
        const val NICKNAME = "nickname"
        const val PROVIDER = "provider"
        const val ROLE = "role"
    }

    fun generateToken(user: User): AccessToken {
        val value = JWT.create()
            .withIssuer(issuer)
            .withSubject(user.id.toString())
            .withClaim(JwtClaims.EMAIL, user.email)
            .withClaim(JwtClaims.USERNAME, user.username)
            .withClaim(JwtClaims.NICKNAME, user.nickname)
            .withClaim(JwtClaims.PROVIDER, user.provider.name)
            .withClaim(JwtClaims.ROLE, user.role.name)
            .withIssuedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
            .withExpiresAt(LocalDateTime.now().plusSeconds(expiration).atZone(ZoneId.systemDefault()).toInstant())
            .sign(jwtAlgorithm)
        return AccessToken(value)
    }

    fun generateAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val decodedToken = jwtVerifier.verify(token)
        return UsernamePasswordAuthenticationToken(
            generatePrincipal(decodedToken),
            null,
            generateAuthorities(decodedToken.claims)
        );
    }

    fun generatePrincipal(decodedToken: DecodedJWT): User {
        val claims = decodedToken.claims
        val email = claims[JwtClaims.EMAIL]?.asString()
        val username = claims[JwtClaims.USERNAME]?.asString()
        val nickname = claims[JwtClaims.NICKNAME]?.asString()
        val provider = claims[JwtClaims.PROVIDER]?.asString()
        val role = claims[JwtClaims.ROLE]?.asString()

        if (email == null || username == null || nickname == null || provider == null || role == null) {
            throw IllegalArgumentException(ResponseMessage.Auth.INVALID_TOKEN)
        }

        return User(
            id = decodedToken.subject.toLong(),
            email = email,
            username = username,
            nickname = nickname,
            provider = User.Provider.valueOf(provider),
            role = User.Role.valueOf(role),
            privacy = UserPrivacy(),
            meta = UserMeta(
                profile = UserProfile(),
                github = UserGitHub(),
                stats = UserStats(),
            ),
        )
    }

    fun generateAuthorities(claims: Map<String, Claim>): List<GrantedAuthority> =
        claims["role"]!!.asString()
            .let { User.Role.valueOf(it) }
            .let { AuthorityUtils.commaSeparatedStringToAuthorityList(it.getAuthority()) }
}
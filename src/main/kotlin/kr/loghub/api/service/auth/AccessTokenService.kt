package kr.loghub.api.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import kr.loghub.api.constant.ResponseMessage
import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserPrivacy
import kr.loghub.api.entity.user.UserProfile
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class AccessTokenService(
    @Value("\${access-token.issuer}") private val issuer: String,
    @Value("\${access-token.expiration}") private val expiration: Long,
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

    fun generateToken(user: User): String =
        JWT.create()
            .withIssuer(issuer)
            .withSubject(user.id.toString())
            .withClaim(JwtClaims.EMAIL, user.email)
            .withClaim(JwtClaims.USERNAME, user.username)
            .withClaim(JwtClaims.NICKNAME, user.profile.nickname)
            .withClaim(JwtClaims.PROVIDER, user.provider.name)
            .withClaim(JwtClaims.ROLE, user.role.name)
            .withIssuedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
            .withExpiresAt(LocalDateTime.now().plusSeconds(expiration).atZone(ZoneId.systemDefault()).toInstant())
            .sign(jwtAlgorithm)

    fun generateAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val decodedToken = decodeToken(token);
        return UsernamePasswordAuthenticationToken(
            generatePrincipal(decodedToken),
            null,
            generateAuthorities(decodedToken.claims)
        );
    }

    fun generatePrincipal(decodedToken: DecodedJWT): User {
        val claims = decodedToken.claims
        return User(
            id = decodedToken.subject.toLong(),
            email = claims["email"]!!.asString(),
            username = claims["username"]!!.asString(),
            provider = User.Provider.valueOf(claims["provider"]!!.asString()),
            profile = UserProfile(nickname = claims["nickname"]!!.asString()),
            privacy = UserPrivacy(),
            role = User.Role.valueOf(claims["role"]!!.asString()),
        )
    }

    fun generateAuthorities(claims: Map<String, Claim>): List<GrantedAuthority> =
        claims["role"]!!.asString()
            .let { User.Role.valueOf(it) }
            .let { AuthorityUtils.commaSeparatedStringToAuthorityList(it.getAuthority()) }

    private fun decodeToken(token: String): DecodedJWT {
        val decodedToken = jwtVerifier.verify(token)
        if (decodedToken.expiresAt.before(Date(System.currentTimeMillis()))) {
            throw TokenExpiredException(ResponseMessage.EXPIRED_TOKEN, decodedToken.expiresAt.toInstant())
        }
        return decodedToken
    }
}
package kr.loghub.api.service.auth.token

import kr.loghub.api.entity.auth.token.RefreshToken
import kr.loghub.api.entity.user.User
import kr.loghub.api.repository.auth.RefreshTokenRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshTokenService(private val refreshTokenRepository: RefreshTokenRepository) {
    fun generateToken(user: User) = refreshTokenRepository.save(
        RefreshToken(
            token = UUID.randomUUID(),
            user = user,
        )
    ).token
}
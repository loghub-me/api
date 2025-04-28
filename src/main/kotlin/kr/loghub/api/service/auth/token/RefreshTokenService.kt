package kr.loghub.api.service.auth.token

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.auth.token.RefreshToken
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.auth.RefreshTokenRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshTokenService(private val refreshTokenRepository: RefreshTokenRepository) {
    fun generateToken(user: User) = refreshTokenRepository.save(
        RefreshToken(
            token = UUID.randomUUID(),
            userId = user.id ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND),
        )
    ).token
}
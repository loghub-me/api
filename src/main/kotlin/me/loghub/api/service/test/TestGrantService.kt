package me.loghub.api.service.test

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Profile("test")
@Service
class TestGrantService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
) {
    @Transactional(readOnly = true)
    fun grant(username: String): Pair<User, TokenDTO> {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND)
        val token = tokenService.generateToken(user)
        return Pair(user, token)
    }
}
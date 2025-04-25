package kr.loghub.api.repository.auth

import kr.loghub.api.entity.auth.token.RefreshToken
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RefreshTokenRepository : CrudRepository<RefreshToken, UUID> {
    fun findByToken(token: UUID): RefreshToken?
}
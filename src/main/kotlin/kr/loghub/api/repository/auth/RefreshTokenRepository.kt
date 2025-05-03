package kr.loghub.api.repository.auth

import kr.loghub.api.entity.auth.token.RefreshToken
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    @EntityGraph(attributePaths = ["user"])
    fun findByToken(token: UUID): RefreshToken?

    @Modifying
    @Query("UPDATE RefreshToken r SET r.expiredAt = :expiredAt WHERE r.token = :token")
    fun updateExpiredAtByToken(expiredAt: LocalDateTime, token: UUID): Int
}
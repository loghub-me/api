package kr.loghub.api.repository.user

import kr.loghub.api.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}
package me.loghub.api.repository.user

import me.loghub.api.entity.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u._username = :username")
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u WHERE u.id = :id")
    @EntityGraph(attributePaths = ["meta"])
    fun findWithMetaById(id: Long): User?

    @Query("SELECT u FROM User u WHERE u._username = :username")
    @EntityGraph(attributePaths = ["meta"])
    fun findWithMetaByUsername(username: String): User?

    @Query("SELECT COUNT(*) > 0 FROM User u WHERE LOWER(u._username) = LOWER(:username)")
    fun existsByUsernameIgnoreCase(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
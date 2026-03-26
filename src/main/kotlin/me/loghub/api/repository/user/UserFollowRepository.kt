package me.loghub.api.repository.user

import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserFollow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserFollowRepository : JpaRepository<UserFollow, Long> {
    @EntityGraph(attributePaths = ["follower"])
    @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.followee = :followee ORDER BY uf.id DESC")
    fun findFollowersByFolloweeOrderByIdDesc(followee: User, pageable: Pageable): Page<User>

    @EntityGraph(attributePaths = ["followee"])
    @Query("SELECT uf.followee FROM UserFollow uf WHERE uf.follower = :follower ORDER BY uf.id DESC")
    fun findFolloweesByFollowerOrderByIdDesc(follower: User, pageable: Pageable): Page<User>

    @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.followee = :followee")
    fun findFollowersByFollowee(followee: User): List<User>

    fun findByFollowerAndFollowee(follower: User, followee: User): UserFollow?

    fun existsByFollowerAndFollowee(follower: User, followee: User): Boolean
}
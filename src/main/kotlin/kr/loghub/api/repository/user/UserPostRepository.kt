package kr.loghub.api.repository.user

import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserPost
import org.springframework.data.jpa.repository.JpaRepository

interface UserPostRepository : JpaRepository<UserPost, String> {
    fun findTop20ByUserOrderByUpdatedAtDesc(user: User): List<UserPost>
}
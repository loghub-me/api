package kr.loghub.api.service.common

import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserStar

interface IStarService {
    fun existsStar(id: Long, user: User): Boolean

    fun addStar(id: Long, user: User): UserStar

    fun removeStar(id: Long, user: User)
}
package me.loghub.api.service.common

import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar

interface IStarService {
    fun existsStar(id: Long, user: User): Boolean

    fun addStar(id: Long, user: User): UserStar

    fun deleteStar(id: Long, user: User)
}
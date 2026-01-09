package me.loghub.api.service.common

import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar

interface IStarService {
    fun existsStar(id: Long, stargazer: User): Boolean

    fun addStar(id: Long, stargazer: User): UserStar

    fun deleteStar(id: Long, stargazer: User)
}
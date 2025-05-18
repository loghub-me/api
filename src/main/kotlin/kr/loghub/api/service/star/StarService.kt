package kr.loghub.api.service.star

import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User

interface StarService {
    fun existsStar(id: Long, user: User): Boolean

    fun addStar(id: Long, user: User): Star

    fun removeStar(id: Long, user: User)
}
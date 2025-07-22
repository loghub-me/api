package kr.loghub.api.mapper.user

import kr.loghub.api.dto.user.star.UserStarDTO
import kr.loghub.api.entity.user.UserStar
import java.time.format.DateTimeFormatter

object UserStarMapper {
    fun map(star: UserStar): UserStarDTO = when (star.target) {
        UserStar.Target.ARTICLE -> {
            val target = star.article!!
            UserStarDTO(
                id = star.id!!,
                path = "/@${target.writerUsername}/articles/${target.slug}",
                title = target.title,
                writerId = target.writer.id!!,
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }

        UserStar.Target.SERIES -> {
            val target = star.series!!
            UserStarDTO(
                id = star.id!!,
                path = "/@${target.writerUsername}/series/${target.slug}",
                title = target.title,
                writerId = target.writer.id!!,
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }

        UserStar.Target.QUESTION -> {
            val target = star.question!!
            UserStarDTO(
                id = star.id!!,
                path = "/@${target.writerUsername}/questions/${target.slug}",
                title = target.title,
                writerId = target.writer.id!!,
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }
    }
}
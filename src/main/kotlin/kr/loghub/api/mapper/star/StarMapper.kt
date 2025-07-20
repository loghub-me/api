package kr.loghub.api.mapper.star

import kr.loghub.api.dto.star.StarDTO
import kr.loghub.api.dto.user.UserSimpleDTO
import kr.loghub.api.entity.star.Star
import java.time.format.DateTimeFormatter

object StarMapper {
    fun map(star: Star): StarDTO = when (star.target) {
        Star.Target.ARTICLE -> {
            val target = star.article!!
            StarDTO(
                id = star.id!!,
                path = "/@${target.writerUsername}/articles/${target.slug}",
                title = target.title,
                writer = UserSimpleDTO(
                    target.writer.id!!, target.writer.username,
                ),
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }

        Star.Target.SERIES -> {
            val target = star.series!!
            StarDTO(
                id = star.id!!,
                path = "/@${target.writerUsername}/series/${target.slug}",
                title = target.title,
                writer = UserSimpleDTO(
                    target.writer.id!!, target.writer.username,
                ),
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }

        Star.Target.QUESTION -> {
            val target = star.question!!
            StarDTO(
                id = star.id!!,
                path = "/@${target.writerUsername}/questions/${target.slug}",
                title = target.title,
                writer = UserSimpleDTO(
                    target.writer.id!!, target.writer.username,
                ),
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }
    }
}
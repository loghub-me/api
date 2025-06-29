package kr.loghub.api.mapper.star

import kr.loghub.api.dto.star.StarDTO
import kr.loghub.api.entity.star.Star
import java.time.format.DateTimeFormatter

object StarMapper {
    fun map(star: Star): StarDTO = when (star.target) {
        Star.Target.ARTICLE -> {
            val target = star.article!!
            StarDTO(
                id = star.id!!,
                path = "/articles/@${target.writerUsername}/${target.slug}",
                title = target.title,
                writerUsername = target.writerUsername,
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }

        Star.Target.BOOK -> {
            val target = star.book!!
            StarDTO(
                id = star.id!!,
                path = "/books/${target.id}",
                title = target.title,
                writerUsername = target.writerUsername,
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
                path = "/questions/@${target.writerUsername}/${target.slug}",
                title = target.title,
                writerUsername = target.writerUsername,
                topics = target.topicsFlat,
                createdAt = target.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                target = star.target,
                targetLabel = star.target.label
            )
        }
    }
}
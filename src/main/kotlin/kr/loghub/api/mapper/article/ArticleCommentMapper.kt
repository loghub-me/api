package kr.loghub.api.mapper.article

import kr.loghub.api.dto.article.comment.ArticleCommentDTO
import kr.loghub.api.entity.article.ArticleComment
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object ArticleCommentMapper {
    const val DELETED_CONTENT = "[삭제된 댓글입니다.]"

    fun map(comment: ArticleComment) = ArticleCommentDTO(
        id = comment.id ?: TODO(),
        content = if (comment.deleted) DELETED_CONTENT else comment.content,
        deleted = comment.deleted,
        replyCount = comment.replyCount,
        mention = comment.mention?.let(UserMapper::map),
        writer = UserMapper.map(comment.writer),
        createdAt = comment.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = comment.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}
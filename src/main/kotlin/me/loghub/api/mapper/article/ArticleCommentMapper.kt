package me.loghub.api.mapper.article

import me.loghub.api.dto.article.comment.ArticleCommentDTO
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.mapper.user.UserMapper

object ArticleCommentMapper {
    const val DELETED_CONTENT = "[삭제된 댓글입니다.]"

    fun map(comment: ArticleComment) = ArticleCommentDTO(
        id = comment.persistedId,
        content = if (comment.deleted) DELETED_CONTENT else comment.content,
        deleted = comment.deleted,
        replyCount = comment.replyCount,
        mention = comment.mention?.let(UserMapper::map),
        writer = UserMapper.map(comment.writer),
        createdAt = comment.createdAt,
        updatedAt = comment.updatedAt,
    )
}
package kr.loghub.api.dto.article.comment

import kr.loghub.api.dto.user.UserDTO

data class ArticleCommentDTO(
    val id: Long,
    val content: String,
    val deleted: Boolean,
    val replyCount: Int,
    val mention: UserDTO?,
    val writer: UserDTO,
    val createdAt: String,
    val updatedAt: String,
)

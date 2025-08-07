package me.loghub.api.dto.article.comment

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User

data class PostArticleCommentDTO(
    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(max = 255, message = "내용은 255자 이하이어야 합니다.")
    val content: String,

    @field:Min(1, message = "답변 대상이 잘못되었습니다.")
    val parentId: Long?,
) {
    fun toEntity(article: Article, parent: ArticleComment?, writer: User) = ArticleComment(
        content = content,
        article = article,
        parent = parent,
        mention = parent?.writer,
        writer = writer,
    )
}
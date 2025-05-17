package kr.loghub.api.service.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.comment.ArticleCommentDTO
import kr.loghub.api.dto.article.comment.PostArticleCommentDTO
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.article.ArticleComment
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.article.ArticleCommentMapper
import kr.loghub.api.repository.article.ArticleCommentRepository
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.util.checkPermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleCommentService(
    private val articleRepository: ArticleRepository,
    private val articleCommentRepository: ArticleCommentRepository,
    private val userRepository: UserRepository,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    @Transactional(readOnly = true)
    fun getComments(articleId: Long, page: Int): Page<ArticleCommentDTO> {
        check(articleRepository.existsById(articleId)) { ResponseMessage.Article.NOT_FOUND }
        return articleCommentRepository.findByArticleIdAndParentIsNull(
            articleId = articleId,
            pageable = PageRequest.of(
                page - 1,
                DEFAULT_PAGE_SIZE,
                Sort.by(ArticleComment::createdAt.name).descending()
            ),
        ).map(ArticleCommentMapper::map)
    }

    @Transactional(readOnly = true)
    fun getReplies(articleId: Long, commentId: Long): List<ArticleCommentDTO> {
        check(articleCommentRepository.existsById(commentId)) { ResponseMessage.Article.Comment.NOT_FOUND }
        return articleCommentRepository.findByArticleIdAndParentId(articleId, commentId)
            .map(ArticleCommentMapper::map)
    }

    @Transactional
    fun postComment(articleId: Long, requestBody: PostArticleCommentDTO, writer: User): ArticleComment {
        val (article, parent) = findEntitiesToPost(articleId, requestBody.parentId)
        val comment = requestBody.toEntity(article, parent, writer)

        article.incrementCommentCount()
        comment.parent?.incrementReplyCount()
        return articleCommentRepository.save(comment)
    }

    @Transactional
    fun deleteComment(articleId: Long, commentId: Long, writer: User) {
        val comment = articleCommentRepository.findByArticleIdAndId(articleId, commentId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.Comment.NOT_FOUND)

        checkPermission(comment.writer == writer) { ResponseMessage.Article.Comment.PERMISSION_DENIED }

        comment.parent?.decrementReplyCount()
        comment.article.decrementCommentCount()
        comment.delete()
    }

    private fun findEntitiesToPost(
        articleId: Long,
        parentId: Long?,
    ): Pair<Article, ArticleComment?> {
        val article = articleRepository.findById(articleId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Article.NOT_FOUND) }
        val parent: ArticleComment? = parentId?.let { parentId ->
            articleCommentRepository.findById(parentId)
                .orElseThrow { EntityNotFoundException(ResponseMessage.Article.Comment.NOT_FOUND) }
        }
        return Pair(article, parent)
    }
}
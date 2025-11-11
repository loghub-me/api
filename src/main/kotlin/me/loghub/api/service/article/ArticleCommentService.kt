package me.loghub.api.service.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.comment.ArticleCommentDTO
import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.article.ArticleCommentMapper
import me.loghub.api.repository.article.ArticleCommentRepository
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.util.checkField
import me.loghub.api.util.checkPermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleCommentService(
    private val articleRepository: ArticleRepository,
    private val articleCommentRepository: ArticleCommentRepository,
) {
    private companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    @Transactional(readOnly = true)
    fun getComments(articleId: Long, page: Int): Page<ArticleCommentDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        val articleRef = articleRepository.getReferenceById(articleId)
        return articleCommentRepository.findRootComments(
            articleRef,
            PageRequest.of(page - 1, DEFAULT_PAGE_SIZE)
        ).map(ArticleCommentMapper::map)
    }

    @Transactional(readOnly = true)
    fun getReplies(articleId: Long, parentId: Long): List<ArticleCommentDTO> {
        val articleRef = articleRepository.getReferenceById(articleId)
        val parentRef = articleCommentRepository.getReferenceById(parentId)
        return articleCommentRepository.findLeafComments(articleRef, parentRef)
            .map(ArticleCommentMapper::map)
    }

    @Transactional
    fun postComment(articleId: Long, requestBody: PostArticleCommentDTO, writer: User): ArticleComment {
        val article = articleRepository.findWithWriterById(articleId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)
        val parent = getAvailableParent(article, requestBody.parentId)

        val comment = requestBody.toEntity(article, parent, writer)
        val savedComment = articleCommentRepository.save(comment)

        articleRepository.incrementCommentCount(articleId)
        parent?.let { articleCommentRepository.incrementReplyCount(it.id!!) }

        return savedComment
    }

    @Transactional
    fun editComment(
        articleId: Long,
        commentId: Long,
        requestBody: PostArticleCommentDTO,
        writer: User
    ): ArticleComment {
        val article = articleRepository.getReferenceById(articleId)
        val comment = articleCommentRepository.findWithGraphByArticleAndId(article, commentId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.Comment.NOT_FOUND)

        checkPermission(comment.writer.id == writer.id) { ResponseMessage.Article.Comment.PERMISSION_DENIED }

        comment.update(requestBody)
        return comment
    }

    @Transactional
    fun deleteComment(articleId: Long, commentId: Long, writer: User) {
        val article = articleRepository.getReferenceById(articleId)
        val comment = articleCommentRepository.findWithGraphByArticleAndId(article, commentId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.Comment.NOT_FOUND)

        checkPermission(comment.writer.id == writer.id) { ResponseMessage.Article.Comment.PERMISSION_DENIED }

        comment.parent?.let { articleCommentRepository.decrementReplyCount(it.id!!) }
        articleRepository.decrementCommentCount(articleId)
        comment.delete()
    }

    private fun getAvailableParent(article: Article, parentId: Long?): ArticleComment? =
        parentId?.let { id ->
            articleCommentRepository.findWithWriterByArticleAndId(article, id)
                ?: throw EntityNotFoundException(ResponseMessage.Article.Comment.NOT_FOUND)
        }?.also {
            checkField(
                PostArticleCommentDTO::parentId.name,
                !it.deleted
            ) { ResponseMessage.Article.Comment.PARENT_DELETED }
        }.let {
            if (it?.parent != null) {
                return it.parent
            }
            return it
        }
}
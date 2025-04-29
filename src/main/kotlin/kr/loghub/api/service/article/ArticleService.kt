package kr.loghub.api.service.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.dto.article.PostArticleDTO
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.article.ArticleMapper
import kr.loghub.api.repository.article.ArticleCustomRepository
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.topic.TopicRepository
import kr.loghub.api.util.checkField
import kr.loghub.api.util.checkPermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleCustomRepository: ArticleCustomRepository,
    private val topicRepository: TopicRepository,
) {
    companion object {
        private const val PAGE_SIZE = 30
    }

    @Transactional(readOnly = true)
    fun searchArticles(query: String, sort: ArticleSort, page: Int): Page<ArticleDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return articleCustomRepository.search(
            query = query.trim(),
            sort = sort,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(ArticleMapper::map)
    }

    @Transactional(readOnly = true)
    fun getArticle(username: String, slug: String) = articleRepository.findByCompositeKey(username, slug)
        ?.let(ArticleMapper::mapDetail)
        ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

    @Transactional
    fun postArticle(requestBody: PostArticleDTO, writer: User): Article {
        requestBody.slug = generateUniqueSlug(writer.username, requestBody.slug)

        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)
            .ifEmpty { throw EntityNotFoundException(ResponseMessage.Topic.NOT_FOUND) }
        val article = requestBody.toEntity(writer, topics)
        return articleRepository.save(article)
    }

    @Transactional
    fun editArticle(username: String, slug: String, requestBody: PostArticleDTO, writer: User): Article {
        val article = articleRepository.findByCompositeKey(username, slug)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED } }
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)
            .ifEmpty { throw EntityNotFoundException(ResponseMessage.Topic.NOT_FOUND) }

        requestBody.slug = generateUniqueSlug(writer.username, requestBody.slug)

        article.update(requestBody)
        article.updateTopics(topics)
        return article
    }

    @Transactional
    fun deleteArticle(username: String, slug: String, writer: User) {
        val article = articleRepository.findByCompositeKey(username, slug)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED } }
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

        articleRepository.delete(article)
    }

    private fun generateUniqueSlug(username: String, baseSlug: String): String {
        var slug = baseSlug
        while (articleRepository.existsByCompositeKey(username, slug)) {
            slug = "$slug-${UUID.randomUUID()}"
        }
        return slug
    }
}
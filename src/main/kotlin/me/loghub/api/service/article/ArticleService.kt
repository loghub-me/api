package me.loghub.api.service.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.*
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.article.ArticleMapper
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.topic.TopicRepository
import me.loghub.api.service.common.CacheService
import me.loghub.api.util.SlugBuilder
import me.loghub.api.util.checkField
import me.loghub.api.util.checkPermission
import me.loghub.api.util.toSlug
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleCustomRepository: ArticleCustomRepository,
    private val topicRepository: TopicRepository,
    private val cacheService: CacheService,
) {
    private companion object {
        private const val PAGE_SIZE = 20
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
    fun getArticle(username: String, slug: String): ArticleDetailDTO {
        val article = articleRepository.findWithWriterByCompositeKey(username, slug)
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)
        val renderedMarkdown = cacheService.findOrGenerateMarkdownCache(article.content)
        return ArticleMapper.mapDetail(article, renderedMarkdown)
    }

    @Transactional(readOnly = true)
    fun getArticleForEdit(articleId: Long, writer: User): ArticleForEditDTO {
        val article = articleRepository.findWithWriterById(articleId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

        checkPermission(article.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED }

        return ArticleMapper.mapForEdit(article)
    }

    @Transactional
    fun postArticle(requestBody: PostArticleDTO, writer: User): Article {
        val slug = SlugBuilder.generateUniqueSlug(
            slug = requestBody.title.toSlug(),
            exists = { slug -> articleRepository.existsByCompositeKey(writer.username, slug) }
        )
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        val article = requestBody.toEntity(slug, writer, topics)
        return articleRepository.save(article)
    }

    @Transactional
    fun editArticle(articleId: Long, requestBody: PostArticleDTO, writer: User): Article {
        val article = articleRepository.findWithWriterById(articleId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

        checkPermission(article.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED }

        val slug = SlugBuilder.generateUniqueSlug(
            slug = requestBody.title.toSlug(),
            exists = { slug -> articleRepository.existsByCompositeKeyAndIdNot(writer.username, slug, articleId) }
        )
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        article.update(requestBody)
        article.updateSlug(slug)
        article.updateTopics(topics)
        return article
    }

    @Transactional
    fun removeArticle(articleId: Long, writer: User) {
        val article = articleRepository.findWithWriterById(articleId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

        checkPermission(article.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED }

        articleRepository.delete(article)
    }
}
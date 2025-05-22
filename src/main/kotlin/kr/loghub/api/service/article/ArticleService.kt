package kr.loghub.api.service.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleDetailDTO
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.dto.article.PostArticleDTO
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.article.ArticleMapper
import kr.loghub.api.repository.article.ArticleCustomRepository
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.topic.TopicRepository
import kr.loghub.api.service.cache.CacheService
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
    private val cacheService: CacheService,
) {
    companion object {
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
        val cachedHTML = cacheService.findOrGenerateMarkdownCache(article.content)
        return ArticleMapper.mapDetail(article, cachedHTML)
    }

    @Transactional
    fun postArticle(requestBody: PostArticleDTO, writer: User): Article {
        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        val article = requestBody.toEntity(slug, writer, topics)
        return articleRepository.save(article)
    }

    @Transactional
    fun editArticle(articleId: Long, requestBody: PostArticleDTO, writer: User): Article {
        val article = articleRepository.findWithWriterById(articleId)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED } }
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)
        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val topics = topicRepository.findDTOsBySlugIn(requestBody.topicSlugs)

        article.update(requestBody)
        article.updateSlug(slug)
        article.updateTopics(topics)
        return article
    }

    @Transactional
    fun removeArticle(articleId: Long, writer: User) {
        val article = articleRepository.findWithWriterById(articleId)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED } }
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

        articleRepository.delete(article)
    }

    private fun generateUniqueSlug(username: String, title: String): String {
        var slug = title
            .lowercase()
            .replace("%20", "-") // "%20" -> "-"
            .replace(Regex("[^가-힣ㄱ-ㅎㅏ-ㅣa-z0-9-_]"), "-") // 허용 문자 외에는 "-"로 치환
            .replace(Regex("-{2,}"), "-") // 연속된 "-"는 하나로
            .replace(Regex("^-|-$"), "") // 앞뒤 "-" 제거
        while (articleRepository.existsByCompositeKey(username, slug)) {
            slug = "$slug-${UUID.randomUUID()}"
        }
        return slug
    }
}
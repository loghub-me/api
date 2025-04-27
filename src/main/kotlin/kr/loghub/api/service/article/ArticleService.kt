package kr.loghub.api.service.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.ArticleMapper
import kr.loghub.api.repository.article.ArticleCustomRepository
import kr.loghub.api.repository.article.ArticleRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleCustomRepository: ArticleCustomRepository
) {
    companion object {
        private const val PAGE_SIZE = 30
    }

    @Transactional(readOnly = true)
    fun searchArticles(query: String, sort: ArticleSort, page: Int) =
        articleCustomRepository.search(query, sort, PageRequest.of(page - 1, PAGE_SIZE))
            .map(ArticleMapper::map)

    @Transactional(readOnly = true)
    fun getArticle(username: String, slug: String) = articleRepository.findByCompositeKey(username, slug)
        ?.let(ArticleMapper::mapDetail)
        ?: throw EntityNotFoundException(ResponseMessage.ARTICLE_NOT_FOUND)
}
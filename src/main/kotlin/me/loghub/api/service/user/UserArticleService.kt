package me.loghub.api.service.user

import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.entity.user.User
import me.loghub.api.mapper.article.ArticleMapper
import me.loghub.api.repository.article.ArticleCustomRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserArticleService(private val articleCustomRepository: ArticleCustomRepository) {
    private companion object {
        const val DEFAULT_ARTICLE_PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun searchUnpublishedArticles(query: String, user: User) =
        articleCustomRepository.search(
            query = query,
            sort = ArticleSort.latest,
            pageable = PageRequest.of(0, DEFAULT_ARTICLE_PAGE_SIZE),
            username = user.username,
            published = false,
        ).toList().map { ArticleMapper.map(it) }

    @Transactional(readOnly = true)
    fun searchArticlesForImport(query: String, user: User) =
        articleCustomRepository.search(
            query = query,
            sort = ArticleSort.latest,
            pageable = PageRequest.of(0, DEFAULT_ARTICLE_PAGE_SIZE),
            username = user.username,
        ).toList().map { ArticleMapper.mapForImport(it) }
}
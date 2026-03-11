package me.loghub.api.service.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.trending.ArticleTrendingScoreDelta
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.article.ArticleStatsRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.StarService
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleStarService(
    private val userStarRepository: UserStarRepository,
    private val articleRepository: ArticleRepository,
    private val articleStatsRepository: ArticleStatsRepository,
    private val articleTrendingScoreService: ArticleTrendingScoreService,
) : StarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, stargazer: User): Boolean {
        val articleRef = articleRepository.getReferenceById(id)
        return userStarRepository.existsByArticleAndStargazer(articleRef, stargazer)
    }

    @Transactional
    override fun addStar(id: Long, stargazer: User): UserStar {
        val articleRef = articleRepository.getReferenceById(id)

        checkConflict(
            userStarRepository.existsByArticleAndStargazer(articleRef, stargazer)
        ) { ResponseMessage.Star.ALREADY_EXISTS }
        checkExists(
            articleRepository.existsById(id)
        ) { ResponseMessage.Article.NOT_FOUND }

        val newStar = UserStar(stargazer = stargazer, article = articleRef, target = UserStar.Target.ARTICLE)
        val savedStar = userStarRepository.save(newStar)
        articleStatsRepository.incrementStarCount(id);
        articleTrendingScoreService.updateTrendingScore(id, ArticleTrendingScoreDelta.STAR)
        return savedStar
    }

    @Transactional
    override fun deleteStar(id: Long, stargazer: User) {
        val articleRef = articleRepository.getReferenceById(id)
        val deletedRows = userStarRepository.deleteByArticleAndStargazer(articleRef, stargazer)

        checkExists(deletedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        articleStatsRepository.decrementStarCount(id)
        articleTrendingScoreService.updateTrendingScore(id, -ArticleTrendingScoreDelta.STAR)
    }
}
package kr.loghub.api.service.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.star.StarRepository
import kr.loghub.api.service.star.StarService
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleStarService(
    private val starRepository: StarRepository,
    private val articleRepository: ArticleRepository,
) : StarService {
    @Transactional(readOnly = true)
    override fun existsStar(articleId: Long, user: User): Boolean =
        starRepository.existsByArticleIdAndUserId(articleId, user.id!!)

    @Transactional
    override fun addStar(articleId: Long, user: User): Star {
        val article = articleRepository.findById(articleId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Article.NOT_FOUND) }
        checkAlreadyExists(starRepository.existsByArticleIdAndUserId(articleId, user.id!!)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }
        article.incrementStarCount()
        return starRepository.save(Star(user = user, article = article, target = Star.Target.ARTICLE))
    }

    @Transactional
    override fun removeStar(articleId: Long, user: User) {
        val affectedRows = starRepository.deleteByArticleIdAndUserId(articleId, user.id!!)
        checkExists(affectedRows > 0) {
            ResponseMessage.Star.NOT_FOUND
        }
        articleRepository.decrementStarCount(articleId)
    }
}
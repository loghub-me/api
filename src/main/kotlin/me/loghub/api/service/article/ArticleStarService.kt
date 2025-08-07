package me.loghub.api.service.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.IStarService
import me.loghub.api.util.checkAlreadyExists
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleStarService(
    private val userStarRepository: UserStarRepository,
    private val articleRepository: ArticleRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(articleId: Long, user: User): Boolean =
        userStarRepository.existsByArticleIdAndUser(articleId, user)

    @Transactional
    override fun addStar(articleId: Long, user: User): UserStar {
        val article = articleRepository.findById(articleId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Article.NOT_FOUND) }

        checkAlreadyExists(userStarRepository.existsByArticleAndUser(article, user)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }

        article.incrementStarCount()
        return userStarRepository.save(UserStar(user = user, article = article, target = UserStar.Target.ARTICLE))
    }

    @Transactional
    override fun removeStar(articleId: Long, user: User) {
        val affectedRows = userStarRepository.deleteByArticleIdAndUser(articleId, user)

        checkExists(affectedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        articleRepository.decrementStarCount(articleId)
    }
}
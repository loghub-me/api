package me.loghub.api.service.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.IStarService
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleStarService(
    private val userStarRepository: UserStarRepository,
    private val articleRepository: ArticleRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, user: User): Boolean {
        val articleRef = articleRepository.getReferenceById(id)
        return userStarRepository.existsByArticleAndUser(articleRef, user)
    }

    @Transactional
    override fun addStar(id: Long, user: User): UserStar {
        val articleRef = articleRepository.getReferenceById(id)

        checkConflict(
            userStarRepository.existsByArticleAndUser(articleRef, user)
        ) { ResponseMessage.Star.ALREADY_EXISTS }
        checkExists(
            articleRepository.existsById(id)
        ) { ResponseMessage.Article.NOT_FOUND }

        articleRepository.incrementStarCount(id); return userStarRepository.save(
            UserStar(
                user = user,
                article = articleRef,
                target = UserStar.Target.ARTICLE
            )
        )
    }

    @Transactional
    override fun removeStar(id: Long, user: User) {
        val articleRef = articleRepository.getReferenceById(id)
        val deletedRows = userStarRepository.deleteByArticleAndUser(articleRef, user)

        checkExists(deletedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        articleRepository.decrementStarCount(id)
    }
}
package me.loghub.api.service.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.article.ArticleDraftRedisKey
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.util.checkPermission
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleDraftService(
    private val articleRepository: ArticleRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Transactional
    fun updateArticleDraft(articleId: Long, requestBody: UpdateDraftDTO, writer: User) {
        checkPermission(
            articleRepository.existsByIdAndWriter(articleId, writer)
        ) { ResponseMessage.Article.PERMISSION_DENIED }

        val redisKey = ArticleDraftRedisKey(articleId)
        redisTemplate.opsForValue().set(redisKey, requestBody.content, ArticleDraftRedisKey.TTL)
    }

    @Transactional
    fun deleteArticleDraft(articleId: Long, writer: User) {
        checkPermission(
            articleRepository.existsByIdAndWriter(articleId, writer)
        ) { ResponseMessage.Article.PERMISSION_DENIED }

        val redisKey = ArticleDraftRedisKey(articleId)
        redisTemplate.delete(redisKey)
    }
}
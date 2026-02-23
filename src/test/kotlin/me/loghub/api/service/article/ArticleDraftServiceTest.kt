package me.loghub.api.service.article

import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.lib.redis.key.article.ArticleDraftRedisKey
import me.loghub.api.repository.article.ArticleRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class ArticleDraftServiceTest {
    private lateinit var articleRepository: ArticleRepository
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var articleDraftService: ArticleDraftService

    @BeforeEach
    fun setUp() {
        articleRepository = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        articleDraftService = ArticleDraftService(articleRepository, redisTemplate)
    }

    @Nested
    inner class UpdateArticleDraftTest {
        @Test
        fun `should update draft when writer has permission`() {
            val articleId = 1L
            val writer = ArticleFixtures.writer()
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(articleRepository.existsByIdAndWriter(articleId, writer)).thenReturn(true)

            articleDraftService.updateArticleDraft(articleId, requestBody, writer)

            verify(articleRepository).existsByIdAndWriter(articleId, writer)
            verify(valueOperations).set(
                ArticleDraftRedisKey(articleId),
                requestBody.content,
                ArticleDraftRedisKey.TTL
            )
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on update`() {
            val articleId = 1L
            val writer = ArticleFixtures.writer()
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(articleRepository.existsByIdAndWriter(articleId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                articleDraftService.updateArticleDraft(articleId, requestBody, writer)
            }

            verify(articleRepository).existsByIdAndWriter(articleId, writer)
            verify(redisTemplate, never()).opsForValue()
            verifyNoInteractions(valueOperations)
        }
    }

    @Nested
    inner class DeleteArticleDraftTest {
        @Test
        fun `should delete draft when writer has permission`() {
            val articleId = 1L
            val writer = ArticleFixtures.writer()
            whenever(articleRepository.existsByIdAndWriter(articleId, writer)).thenReturn(true)

            articleDraftService.deleteArticleDraft(articleId, writer)

            verify(articleRepository).existsByIdAndWriter(articleId, writer)
            verify(redisTemplate).delete(ArticleDraftRedisKey(articleId))
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on delete`() {
            val articleId = 1L
            val writer = ArticleFixtures.writer()
            whenever(articleRepository.existsByIdAndWriter(articleId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                articleDraftService.deleteArticleDraft(articleId, writer)
            }

            verify(articleRepository).existsByIdAndWriter(articleId, writer)
            verify(redisTemplate, never()).delete(any<String>())
        }
    }
}

package me.loghub.api.service.series

import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.lib.redis.key.series.SeriesChapterDraftRedisKey
import me.loghub.api.repository.series.SeriesChapterRepository
import me.loghub.api.repository.series.SeriesRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class SeriesChapterDraftServiceTest {
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var seriesChapterRepository: SeriesChapterRepository
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var seriesChapterDraftService: SeriesChapterDraftService

    @BeforeEach
    fun setUp() {
        seriesRepository = mock()
        seriesChapterRepository = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        seriesChapterDraftService = SeriesChapterDraftService(
            seriesRepository,
            seriesChapterRepository,
            redisTemplate
        )
    }

    @Nested
    inner class UpdateChapterDraftTest {
        @Test
        fun `should update chapter draft when writer has permission`() {
            val seriesId = 1L
            val chapterId = 2L
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = seriesId, writer = writer)
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(series)
            whenever(seriesChapterRepository.existsBySeriesAndIdAndWriter(series, chapterId, writer)).thenReturn(true)

            seriesChapterDraftService.updateChapterDraft(seriesId, chapterId, requestBody, writer)

            verify(seriesChapterRepository).existsBySeriesAndIdAndWriter(series, chapterId, writer)
            verify(valueOperations).set(
                SeriesChapterDraftRedisKey(seriesId, chapterId),
                requestBody.content,
                SeriesChapterDraftRedisKey.TTL
            )
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on update`() {
            val seriesId = 1L
            val chapterId = 2L
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = seriesId, writer = writer)
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(series)
            whenever(seriesChapterRepository.existsBySeriesAndIdAndWriter(series, chapterId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                seriesChapterDraftService.updateChapterDraft(seriesId, chapterId, requestBody, writer)
            }

            verify(redisTemplate, never()).opsForValue()
            verifyNoInteractions(valueOperations)
        }
    }

    @Nested
    inner class DeleteChapterDraftTest {
        @Test
        fun `should delete chapter draft when writer has permission`() {
            val seriesId = 1L
            val chapterId = 2L
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = seriesId, writer = writer)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(series)
            whenever(seriesChapterRepository.existsBySeriesAndIdAndWriter(series, chapterId, writer)).thenReturn(true)

            seriesChapterDraftService.deleteChapterDraft(seriesId, chapterId, writer)

            verify(redisTemplate).delete(SeriesChapterDraftRedisKey(seriesId, chapterId))
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on delete`() {
            val seriesId = 1L
            val chapterId = 2L
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = seriesId, writer = writer)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(series)
            whenever(seriesChapterRepository.existsBySeriesAndIdAndWriter(series, chapterId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                seriesChapterDraftService.deleteChapterDraft(seriesId, chapterId, writer)
            }

            verify(redisTemplate, never()).delete(any<String>())
        }
    }
}

package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.repository.series.SeriesChapterRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.util.checkPermission
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesChapterDraftService(
    private val seriesRepository: SeriesRepository,
    private val seriesChapterRepository: SeriesChapterRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Transactional
    fun updateChapterDraft(seriesId: Long, chapterId: Long, requestBody: UpdateDraftDTO, writer: User) {
        val series = seriesRepository.getReferenceById(seriesId)
        checkPermission(
            seriesChapterRepository.existsBySeriesAndIdAndWriter(series, chapterId, writer)
        ) { ResponseMessage.Series.PERMISSION_DENIED }

        val redisKey = RedisKeys.Series.Chapter.DRAFT(seriesId, chapterId)
        redisTemplate.opsForValue().set(redisKey.key, requestBody.content, redisKey.ttl)
    }

    @Transactional
    fun deleteChapterDraft(seriesId: Long, chapterId: Long, writer: User) {
        val series = seriesRepository.getReferenceById(seriesId)
        checkPermission(
            seriesChapterRepository.existsBySeriesAndIdAndWriter(series, chapterId, writer)
        ) { ResponseMessage.Series.PERMISSION_DENIED }

        val redisKey = RedisKeys.Series.Chapter.DRAFT(seriesId, chapterId)
        redisTemplate.delete(redisKey.key)
    }
}
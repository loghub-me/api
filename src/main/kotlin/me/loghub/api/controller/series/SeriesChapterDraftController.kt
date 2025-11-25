package me.loghub.api.controller.series

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.series.SeriesChapterDraftService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/series/{seriesId}/chapters/{chapterId}/draft")
class SeriesChapterDraftController(private val seriesChapterDraftService: SeriesChapterDraftService) {
    @PatchMapping
    fun updateChapterDraft(
        @PathVariable seriesId: Long,
        @PathVariable chapterId: Long,
        @RequestBody @Valid requestBody: UpdateDraftDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesChapterDraftService.updateChapterDraft(seriesId, chapterId, requestBody, writer)
        return MessageResponseBody(
            message = ResponseMessage.Series.Chapter.Draft.UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun deleteChapterDraft(
        @PathVariable seriesId: Long,
        @PathVariable chapterId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesChapterDraftService.deleteChapterDraft(seriesId, chapterId, writer)
        return MessageResponseBody(
            message = ResponseMessage.Series.Chapter.Draft.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
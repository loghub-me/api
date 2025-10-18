package me.loghub.api.controller.series

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.MethodResponseBody
import me.loghub.api.dto.response.RedirectResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.SeriesChapterForEditDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.series.SeriesChapterService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/series/{seriesId}/chapters")
class SeriesChapterController(private val seriesChapterService: SeriesChapterService) {
    @GetMapping("/{sequence}")
    fun getChapter(@PathVariable seriesId: Long, @PathVariable sequence: Int): ResponseEntity<SeriesChapterDetailDTO> {
        val chapter = seriesChapterService.getChapter(seriesId, sequence)
        return ResponseEntity.ok(chapter)
    }

    @GetMapping("/{sequence}/for-edit")
    fun getChapterForEdit(
        @PathVariable seriesId: Long,
        @PathVariable sequence: Int,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<SeriesChapterForEditDTO> {
        val foundSeries = seriesChapterService.getChapterForEdit(seriesId, sequence, writer)
        return ResponseEntity.ok(foundSeries)
    }

    @PostMapping
    fun createChapter(
        @PathVariable seriesId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdChapter = seriesChapterService.createChapter(seriesId, writer)
        return MethodResponseBody(
            id = createdChapter.id!!,
            message = ResponseMessage.Series.Chapter.CREATE_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PostMapping("/import/{articleId}")
    fun importChapter(
        @PathVariable seriesId: Long,
        @PathVariable articleId: Long,
        @AuthenticationPrincipal writer: User,
    ): ResponseEntity<ResponseBody> {
        val importedChapter = seriesChapterService.importChapter(seriesId, articleId, writer)
        return MethodResponseBody(
            id = importedChapter.id!!,
            message = ResponseMessage.Series.Chapter.IMPORT_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{sequence}")
    fun editChapter(
        @PathVariable seriesId: Long,
        @PathVariable sequence: Int,
        @RequestBody @Valid requestBody: EditSeriesChapterDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedChapter = seriesChapterService.editChapter(seriesId, sequence, requestBody, writer)
        return RedirectResponseBody(
            pathname = "/edit/series/${updatedChapter.series.id}",
            message = ResponseMessage.Series.Chapter.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{sequence}")
    fun deleteChapter(
        @PathVariable seriesId: Long,
        @PathVariable sequence: Int,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesChapterService.deleteChapter(seriesId, sequence, writer)
        return MessageResponseBody(
            message = ResponseMessage.Series.Chapter.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PatchMapping("/sequence")
    fun updateChapterSequence(
        @PathVariable seriesId: Long,
        @RequestBody @Valid requestBody: UpdateSeriesChapterSequenceDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesChapterService.updateChapterSequence(seriesId, requestBody, writer)
        return MessageResponseBody(
            message = ResponseMessage.Series.Chapter.CHANGE_SEQUENCE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
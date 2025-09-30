package me.loghub.api.controller.series

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.RedirectResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.series.*
import me.loghub.api.entity.user.User
import me.loghub.api.service.series.SeriesService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/series")
class SeriesController(private val seriesService: SeriesService) {
    @GetMapping
    fun searchSeries(
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: SeriesSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<SeriesDTO>> {
        val foundSeries = seriesService.searchSeries(query, sort, page)
        return ResponseEntity.ok(foundSeries)
    }

    @GetMapping("/@{username}/{slug}")
    fun getSeries(@PathVariable username: String, @PathVariable slug: String): ResponseEntity<SeriesDetailDTO> {
        val foundSeries = seriesService.getSeries(username, slug)
        return ResponseEntity.ok(foundSeries)
    }

    @GetMapping("/{id}/edit")
    fun getSeriesForEdit(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<SeriesForEditDTO> {
        val foundSeries = seriesService.getSeriesForEdit(id, writer)
        return ResponseEntity.ok(foundSeries)
    }

    @PostMapping
    fun postSeries(
        @RequestBody @Valid requestBody: PostSeriesDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdSeries = seriesService.postSeries(requestBody, writer)
        return RedirectResponseBody(
            pathname = "/edit/series/${createdSeries.id}",
            message = ResponseMessage.Series.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{id}")
    fun editSeries(
        @PathVariable id: Long,
        @RequestBody @Valid requestBody: PostSeriesDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedSeries = seriesService.editSeries(id, requestBody, writer)
        return RedirectResponseBody(
            pathname = "/series/${writer.username}/${updatedSeries.slug}",
            message = ResponseMessage.Series.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{id}")
    fun deleteSeries(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesService.deleteSeries(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Series.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
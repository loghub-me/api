package me.loghub.api.controller.article

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.article.ArticleDraftService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/articles/{id}/draft")
class ArticleDraftController(private val articleDraftService: ArticleDraftService) {
    @PatchMapping
    fun updateArticleDraft(
        @PathVariable id: Long,
        @RequestBody @Valid requestBody: UpdateDraftDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        articleDraftService.updateArticleDraft(id, requestBody, writer)
        return MessageResponseBody(
            message = ResponseMessage.Draft.UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun deleteArticleDraft(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        articleDraftService.deleteArticleDraft(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Draft.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
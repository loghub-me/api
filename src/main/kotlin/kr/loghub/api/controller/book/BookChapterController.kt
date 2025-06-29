package kr.loghub.api.controller.book

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.book.chapter.BookChapterDetailDTO
import kr.loghub.api.dto.book.chapter.EditBookChapterDTO
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.book.BookChapterService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/{bookId}/chapters")
class BookChapterController(private val bookChapterService: BookChapterService) {
    @GetMapping("/{sequence}")
    fun getChapter(@PathVariable bookId: Long, @PathVariable sequence: Int): ResponseEntity<BookChapterDetailDTO> {
        val chapter = bookChapterService.getChapter(bookId, sequence)
        return ResponseEntity.ok(chapter)
    }

    @PostMapping
    fun createChapter(@PathVariable bookId: Long, @AuthenticationPrincipal writer: User): ResponseEntity<ResponseBody> {
        bookChapterService.createChapter(bookId, writer)
        return MessageResponseBody(
            message = ResponseMessage.Book.Chapter.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{sequence}")
    fun editChapter(
        @PathVariable bookId: Long,
        @PathVariable sequence: Int,
        @RequestBody @Valid requestBody: EditBookChapterDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        bookChapterService.editChapter(bookId, sequence, requestBody, writer)
        return MessageResponseBody(
            message = ResponseMessage.Book.Chapter.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{sequence}")
    fun deleteChapter(
        @PathVariable bookId: Long,
        @PathVariable sequence: Int,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        bookChapterService.deleteChapter(bookId, sequence, writer)
        return MessageResponseBody(
            message = ResponseMessage.Book.Chapter.DELETE_SUCCESS,
            status = HttpStatus.NO_CONTENT,
        ).toResponseEntity()
    }

    @PutMapping("/{sequenceA}/sequence/{sequenceB}")
    fun changeChapterSequence(
        @PathVariable bookId: Long,
        @PathVariable sequenceA: Int,
        @PathVariable sequenceB: Int,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        bookChapterService.changeChapterSequence(bookId, sequenceA, sequenceB, writer)
        return MessageResponseBody(
            message = ResponseMessage.Book.Chapter.CHANGE_SEQUENCE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
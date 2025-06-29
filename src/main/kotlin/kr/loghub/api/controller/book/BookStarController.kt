package kr.loghub.api.controller.book

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.DataResponseBody
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.book.BookStarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/star/{bookId}")
class BookStarController(private val bookStarService: BookStarService) {
    @GetMapping
    fun existsBookStar(
        @PathVariable bookId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        val exists = bookStarService.existsStar(bookId, user)
        return DataResponseBody(
            data = exists,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping
    fun addBookStar(
        @PathVariable bookId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        val star = bookStarService.addStar(bookId, user)
        return MethodResponseBody(
            id = star.id!!,
            message = ResponseMessage.Star.ADD_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun removeBookStar(
        @PathVariable bookId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        bookStarService.removeStar(bookId, user)
        return MessageResponseBody(
            message = ResponseMessage.Star.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
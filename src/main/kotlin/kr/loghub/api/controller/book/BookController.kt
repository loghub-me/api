package kr.loghub.api.controller.book

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.book.BookDTO
import kr.loghub.api.dto.book.BookDetailDTO
import kr.loghub.api.dto.book.BookSort
import kr.loghub.api.dto.book.PostBookDTO
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.RedirectResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.book.BookService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {
    @GetMapping
    fun searchBooks(
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: BookSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<BookDTO>> {
        val foundBooks = bookService.searchBooks(query, sort, page)
        return ResponseEntity.ok(foundBooks)
    }

    @GetMapping("/@{username}/{slug}")
    fun getBook(@PathVariable username: String, @PathVariable slug: String): ResponseEntity<BookDetailDTO> {
        val foundBook = bookService.getBook(username, slug)
        return ResponseEntity.ok(foundBook)
    }

    @PostMapping
    fun postBook(
        @RequestBody @Valid requestBody: PostBookDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdBook = bookService.postBook(requestBody, writer)
        return RedirectResponseBody(
            pathname = "/books/@${writer.username}/${createdBook.slug}/edit",
            message = ResponseMessage.Book.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{id}")
    fun editBook(
        @PathVariable id: Long,
        @RequestBody @Valid requestBody: PostBookDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedBook = bookService.editBook(id, requestBody, writer)
        return RedirectResponseBody(
            pathname = "/books/@${writer.username}/${updatedBook.slug}",
            message = ResponseMessage.Book.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{id}")
    fun removeBook(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        bookService.removeBook(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Book.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
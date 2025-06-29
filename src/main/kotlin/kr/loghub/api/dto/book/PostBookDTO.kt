package kr.loghub.api.dto.book

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.validation.Trimmed

data class PostBookDTO(
    @field:NotBlank(message = "제목은 필수 입력 항목입니다.")
    @field:Size(max = 255, message = "제목은 255자 이내여야 합니다.")
    @field:Trimmed
    val title: String,

    @field:NotBlank(message = "설명은 필수 입력 항목입니다.")
    @field:Size(max = 25565, message = "설명은 25,565자 이내여야 합니다.")
    val content: String,

    @field:NotBlank(message = "썸네일은 필수 입력 항목입니다.")
    @field:Trimmed
    val thumbnail: String,
) {
    fun toEntity(slug: String, writer: User) = Book(
        slug = slug,
        title = title,
        content = content,
        thumbnail = thumbnail,
        writer = writer,
        writerUsername = writer.username,
        topics = mutableSetOf(),
        topicsFlat = mutableListOf()
    )
}
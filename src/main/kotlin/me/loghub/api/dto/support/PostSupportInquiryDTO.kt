package me.loghub.api.dto.support

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.loghub.api.dto.task.discord.DiscordEmbed
import me.loghub.api.entity.support.SupportInquiry
import me.loghub.api.lib.validation.Trimmed

data class PostSupportInquiryDTO(
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    val email: String?,

    @field:NotBlank(message = "제목은 필수 입력 항목입니다.")
    @field:Size(min = 2, max = 56, message = "제목은 2자 이상 56자 이하이어야 합니다.")
    @field:Trimmed
    val title: String,

    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(min = 10, max = 8192, message = "내용은 10자 이상 8192자 이하이어야 합니다.")
    val content: String,
) {
    fun toEntity() = SupportInquiry(
        email = email,
        title = title,
        content = content,
    )

    fun toDiscordEmbed() = DiscordEmbed(
        title = title,
        description = content,
        author = if (email != null) DiscordEmbed.Author(email) else null,
    )
}
package me.loghub.api.dto.support

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.loghub.api.dto.task.discord.DiscordEmbed
import me.loghub.api.lib.validation.Trimmed

data class PostSupportTopicRequestDTO(
    @field:NotBlank(message = "토픽 이름은 필수 입력 항목입니다.")
    @field:Size(min = 1, max = 56, message = "토픽 이름은 1자 이상 56자 이하이어야 합니다.")
    @field:Trimmed
    val name: String,

    @field:NotBlank(message = "토픽 설명은 필수 입력 항목입니다.")
    @field:Size(min = 10, max = 8192, message = "토픽 설명은 10자 이상 8192자 이하이어야 합니다.")
    val description: String,
) {
    fun toDiscordEmbed() = DiscordEmbed(
        title = name,
        description = description,
    )
}
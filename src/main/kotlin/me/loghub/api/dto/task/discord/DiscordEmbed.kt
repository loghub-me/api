package me.loghub.api.dto.task.discord

data class DiscordEmbed(
    val title: String,
    val description: String,
    val color: Int = DEFAULT_COLOR,
    val author: Author? = null,
) {
    companion object {
        const val DEFAULT_COLOR = 0x488bfc
    }

    data class Author(val name: String)
}

package kr.loghub.api.dto.task.avatar

data class AvatarRenameRequest(
    val oldUsername: String,
    val newUsername: String,
)
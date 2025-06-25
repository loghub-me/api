package kr.loghub.api.dto.internal.avatar

data class AvatarRenameRequest(
    val oldUsername: String,
    val newUsername: String,
)
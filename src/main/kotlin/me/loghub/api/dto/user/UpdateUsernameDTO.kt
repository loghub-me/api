package me.loghub.api.dto.user

import me.loghub.api.lib.validation.UsernameValidation

data class UpdateUsernameDTO(
    @field:UsernameValidation
    val newUsername: String,
)

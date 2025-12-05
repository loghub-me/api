package me.loghub.api.dto.auth.join

import me.loghub.api.lib.validation.EmailValidation
import me.loghub.api.lib.validation.NicknameValidation
import me.loghub.api.lib.validation.UsernameValidation

data class JoinRequestDTO(
    @field:EmailValidation
    val email: String,

    @field:UsernameValidation
    val username: String,

    @field:NicknameValidation
    val nickname: String,
)

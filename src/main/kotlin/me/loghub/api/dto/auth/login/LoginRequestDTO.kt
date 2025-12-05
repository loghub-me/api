package me.loghub.api.dto.auth.login

import me.loghub.api.lib.validation.EmailValidation

data class LoginRequestDTO(
    @field:EmailValidation
    val email: String,
) {
}

package me.loghub.api.dto.auth.login

import me.loghub.api.lib.validation.EmailValidation
import me.loghub.api.lib.validation.OTPValidation

data class LoginConfirmDTO(
    @field:EmailValidation
    val email: String,

    @field:OTPValidation
    val otp: String,
)

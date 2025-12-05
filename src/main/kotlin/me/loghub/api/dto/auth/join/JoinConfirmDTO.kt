package me.loghub.api.dto.auth.join

import me.loghub.api.lib.validation.EmailValidation
import me.loghub.api.lib.validation.OTPValidation

data class JoinConfirmDTO(
    @field:EmailValidation
    val email: String,

    @field:OTPValidation
    val otp: String,
)

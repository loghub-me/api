package me.loghub.api.dto.common

import me.loghub.api.lib.validation.ContentValidation

data class UpdateDraftDTO(
    @field:ContentValidation
    val content: String,
)
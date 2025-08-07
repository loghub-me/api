package me.loghub.api.dto.user

import jakarta.validation.constraints.NotNull

data class UpdateUserPrivacyDTO(
    @field:NotNull(message = "이메일 공개 여부는 필수 입력값입니다.")
    val emailVisible: Boolean,

    @field:NotNull(message = "스타 공개 여부는 필수 입력값입니다.")
    val starVisible: Boolean,
)

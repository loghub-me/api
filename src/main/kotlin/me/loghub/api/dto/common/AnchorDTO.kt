package me.loghub.api.dto.common

import com.fasterxml.jackson.annotation.JsonProperty

data class AnchorDTO(
    @JsonProperty("level") val level: Int,
    @JsonProperty("slug") val slug: String,
    @JsonProperty("text") val text: String,
)
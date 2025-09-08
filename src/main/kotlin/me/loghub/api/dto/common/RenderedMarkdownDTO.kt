package me.loghub.api.dto.common

import com.fasterxml.jackson.annotation.JsonProperty

data class RenderedMarkdownDTO(
    @JsonProperty("html") val html: String,
    @JsonProperty("anchors") val anchors: List<AnchorDTO>,
)
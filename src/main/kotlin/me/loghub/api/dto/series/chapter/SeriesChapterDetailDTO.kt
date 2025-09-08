package me.loghub.api.dto.series.chapter

import me.loghub.api.dto.common.AnchorDTO
import me.loghub.api.dto.common.ContentDTO

data class SeriesChapterDetailDTO(
    val id: Long,
    val title: String,
    val content: ContentDTO,
    val anchors: List<AnchorDTO>,
    val sequence: Int,
    val createdAt: String,
    val updatedAt: String,
)
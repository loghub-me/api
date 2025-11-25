package me.loghub.api.dto.question

data class QuestionForEditDTO(
    val id: Long,
    val title: String,
    val content: String,
    val draft: String?,
    val topicSlugs: List<String>,
)

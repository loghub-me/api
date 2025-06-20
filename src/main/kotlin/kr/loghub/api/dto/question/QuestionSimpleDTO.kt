package kr.loghub.api.dto.question

data class QuestionSimpleDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val writerUsername: String,
)

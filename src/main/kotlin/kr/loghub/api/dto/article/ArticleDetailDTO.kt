package kr.loghub.api.dto.article

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.user.UserDTO

data class ArticleDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: ArticleContentDTO,
    val thumbnail: String,
    val writer: UserDTO,
    val stats: ArticleStatsDTO,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
) {
    data class ArticleContentDTO(
        var markdown: String,
        val html: String,
    )
}

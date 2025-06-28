package kr.loghub.api.repository.topic

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.topic.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TopicRepository : JpaRepository<Topic, Long> {
    companion object {
        const val SELECT_TOPIC_DTO = "SELECT new kr.loghub.api.dto.topic.TopicDTO(t.slug, t.name) FROM Topic t"
        const val BY_SLUGS = "WHERE t.slug IN :slugs"
    }

    fun findBySlugIn(slugs: List<String>): Set<Topic>

    fun findBySlug(slug: String): Topic?

    fun findTop20ByOrderByTrendingScoreDesc(): List<Topic>

    fun findByNameContainingIgnoreCaseOrderByTrendingScoreDe(name: String): List<Topic>

    @Query("$SELECT_TOPIC_DTO $BY_SLUGS")
    fun findDTOsBySlugIn(slugs: List<String>): List<TopicDTO>
}
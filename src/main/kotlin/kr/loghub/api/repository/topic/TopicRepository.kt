package kr.loghub.api.repository.topic

import kr.loghub.api.entity.topic.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findBySlugIn(slugs: List<String>): Set<Topic>
}
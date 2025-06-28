package kr.loghub.api.controller.topic

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.topic.TopicDetailDTO
import kr.loghub.api.service.topic.TopicService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/topics")
class TopicController(private val topicService: TopicService) {
    @GetMapping("/trending")
    fun getTrendingTopics(@RequestParam(defaultValue = "") query: String): ResponseEntity<List<TopicDTO>> {
        val topics = topicService.getTrendingArticles(query)
        return ResponseEntity.ok(topics)
    }

    @GetMapping("/{slug}")
    fun getTopic(@PathVariable slug: String): ResponseEntity<TopicDetailDTO> {
        val topic = topicService.getTopic(slug)
        return ResponseEntity.ok(topic)
    }
}
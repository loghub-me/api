package me.loghub.api.controller.topic

import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.topic.TopicDetailDTO
import me.loghub.api.service.topic.TopicService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/topics")
class TopicController(private val topicService: TopicService) {
    @GetMapping("/trending")
    fun getTrendingTopics(): ResponseEntity<List<TopicDTO>> {
        val topics = topicService.getTrendingTopics()
        return ResponseEntity.ok(topics)
    }

    @GetMapping("/{slug}")
    fun getTopic(@PathVariable slug: String): ResponseEntity<TopicDetailDTO> {
        val topic = topicService.getTopic(slug)
        return ResponseEntity.ok(topic)
    }

    @GetMapping("/{slug}/articles/trending")
    fun getTrendingArticles(@PathVariable slug: String): ResponseEntity<List<ArticleDTO>> {
        val articles = topicService.getTrendingArticles(slug)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/{slug}/series/trending")
    fun getTrendingSeries(@PathVariable slug: String): ResponseEntity<List<SeriesDTO>> {
        val series = topicService.getTrendingSeries(slug)
        return ResponseEntity.ok(series)
    }

    @GetMapping("/{slug}/questions/trending")
    fun getTrendingQuestions(@PathVariable slug: String): ResponseEntity<List<QuestionDTO>> {
        val questions = topicService.getTrendingQuestions(slug)
        return ResponseEntity.ok(questions)
    }
}
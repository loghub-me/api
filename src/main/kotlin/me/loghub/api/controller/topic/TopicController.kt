package me.loghub.api.controller.topic

import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.topic.*
import me.loghub.api.service.topic.TopicService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{slug}/articles")
    fun getTopicArticles(
        @PathVariable slug: String,
        @RequestParam(defaultValue = "trending") sort: TopicArticleSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<ArticleDTO>> {
        val articles = topicService.getTopicArticles(slug, sort, page)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/{slug}/series")
    fun getTopicSeries(
        @PathVariable slug: String,
        @RequestParam(defaultValue = "trending") sort: TopicSeriesSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<SeriesDTO>> {
        val series = topicService.getTopicSeries(slug, sort, page)
        return ResponseEntity.ok(series)
    }

    @GetMapping("/{slug}/questions")
    fun getTopicQuestions(
        @PathVariable slug: String,
        @RequestParam(defaultValue = "trending") sort: TopicQuestionSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<QuestionDTO>> {
        val questions = topicService.getTopicQuestions(slug, sort, page)
        return ResponseEntity.ok(questions)
    }
}
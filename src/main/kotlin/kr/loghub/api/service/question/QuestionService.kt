package kr.loghub.api.service.question

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.question.*
import kr.loghub.api.dto.task.answer.AnswerGenerateRequest
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.question.QuestionStats
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.question.QuestionMapper
import kr.loghub.api.repository.question.QuestionCustomRepository
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.topic.TopicRepository
import kr.loghub.api.service.cache.CacheService
import kr.loghub.api.util.checkField
import kr.loghub.api.util.checkPermission
import kr.loghub.api.util.toSlug
import kr.loghub.api.worker.AnswerGenerateWorker
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionCustomRepository: QuestionCustomRepository,
    private val topicRepository: TopicRepository,
    private val cacheService: CacheService,
    private val answerGenerateWorker: AnswerGenerateWorker,
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun searchQuestions(query: String, sort: QuestionSort, filter: QuestionFilter, page: Int): Page<QuestionDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return questionCustomRepository.search(
            query = query.trim(),
            sort = sort,
            filter = filter,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(QuestionMapper::map)
    }

    @Transactional(readOnly = true)
    fun getTrendingQuestions(): List<QuestionDTO> = questionRepository.findAll(
        PageRequest.of(
            0,
            PAGE_SIZE,
            Sort.by("${Question::stats.name}.${QuestionStats::trendingScore.name}").descending(),
        )
    ).toList().map(QuestionMapper::map)

    @Transactional(readOnly = true)
    fun getQuestion(username: String, slug: String): QuestionDetailDTO {
        val question = questionRepository.findWithWriterAndAnswersByCompositeKey(username, slug)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)
        val questionHTML = cacheService.findOrGenerateMarkdownCache(question.content)
        val answerMarkdowns = question.answers.map { it.content }
        val answerHTMLs = cacheService.findOrGenerateMarkdownCache(answerMarkdowns)

        return QuestionMapper.mapDetail(question, questionHTML, answerHTMLs)
    }

    @Transactional
    fun postQuestion(requestBody: PostQuestionDTO, writer: User): Question {
        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        val question = requestBody.toEntity(slug, writer, topics)
        val savedQuestion = questionRepository.save(question)

        if (requestBody.requestBotAnswer) {
            answerGenerateWorker.addToQueue(
                AnswerGenerateRequest(
                    questionId = savedQuestion.id!!,
                    questionContent = "${savedQuestion.title}\n\n${savedQuestion.content}",
                )
            )
        }

        return savedQuestion
    }

    @Transactional
    fun editQuestion(questionId: Long, requestBody: PostQuestionDTO, writer: User): Question {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }

        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        question.update(requestBody)
        question.updateSlug(slug)
        question.updateTopics(topics)
        return question
    }

    @Transactional
    fun removeQuestion(questionId: Long, writer: User) {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }

        questionRepository.delete(question)
    }

    @Transactional
    fun closeQuestion(questionId: Long, writer: User): Question {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        checkField(
            Question::status.name,
            question.status == Question.Status.OPEN
        ) { ResponseMessage.Question.STATUS_MUST_BE_OPEN }

        question.close()
        return question
    }

    private fun generateUniqueSlug(username: String, title: String): String {
        var slug = title.toSlug()
        while (questionRepository.existsByCompositeKey(username, slug)) {
            slug = "$slug-${UUID.randomUUID()}"
        }
        return slug
    }
}
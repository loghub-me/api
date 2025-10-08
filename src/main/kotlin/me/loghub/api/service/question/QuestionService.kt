package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.question.*
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.question.QuestionMapper
import me.loghub.api.repository.question.QuestionCustomRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.topic.TopicRepository
import me.loghub.api.service.common.CacheService
import me.loghub.api.util.SlugBuilder
import me.loghub.api.util.checkField
import me.loghub.api.util.checkPermission
import me.loghub.api.util.toSlug
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionCustomRepository: QuestionCustomRepository,
    private val topicRepository: TopicRepository,
    private val cacheService: CacheService,
) {
    private companion object {
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
    fun getQuestion(username: String, slug: String): QuestionDetailDTO {
        val question = questionRepository.findWithWriterByCompositeKey(username, slug)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)
        val renderedMarkdown = cacheService.findOrGenerateMarkdownCache(question.content)

        return QuestionMapper.mapDetail(question, renderedMarkdown)
    }

    @Transactional(readOnly = true)
    fun getQuestionAnswerGenerating(questionId: Long) =
        questionRepository.findAnswerGeneratingById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

    @Transactional(readOnly = true)
    fun getQuestionForEdit(questionId: Long, writer: User): QuestionForEditDTO {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }

        return QuestionMapper.mapForEdit(question)
    }

    @Transactional
    fun postQuestion(requestBody: PostQuestionDTO, writer: User): Question {
        val slug = SlugBuilder.generateUniqueSlug(
            slug = requestBody.title.toSlug(),
            exists = { slug -> questionRepository.existsByCompositeKey(writer.username, slug) }
        )
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        val question = requestBody.toEntity(slug, writer, topics)
        return questionRepository.save(question)
    }

    @Transactional
    fun editQuestion(questionId: Long, requestBody: PostQuestionDTO, writer: User): Question {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }

        val slug = SlugBuilder.generateUniqueSlug(
            slug = requestBody.title.toSlug(),
            exists = { slug -> questionRepository.existsByCompositeKeyAndIdNot(writer.username, slug, questionId) }
        )
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        question.update(requestBody)
        question.updateSlug(slug)
        question.updateTopics(topics)
        return question
    }

    @Transactional
    fun deleteQuestion(questionId: Long, writer: User) {
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
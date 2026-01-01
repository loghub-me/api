package me.loghub.api.service.question

import feign.FeignException
import me.loghub.api.config.AsyncConfig
import me.loghub.api.constant.ai.Bot
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.dto.task.answer.AnswerGenerateResponse
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.lib.redis.key.question.QuestionAnswerGenerateCooldownRedisKey
import me.loghub.api.lib.redis.key.question.QuestionAnswerGeneratingRedisKey
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.orElseThrowNotFound
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionAnswerGenerateService(
    private val questionRepository: QuestionRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val userRepository: UserRepository,
    private val taskAPIProxy: TaskAPIProxy,
) {
    companion object {
        private const val REJECTION_TITLE = "답변이 거절되었습니다"
    }

    fun checkGeneratingAnswer(questionId: Long): Boolean =
        redisTemplate.hasKey(QuestionAnswerGeneratingRedisKey(questionId))

    fun checkGenerateCooldown(questionId: Long): Boolean =
        redisTemplate.hasKey(QuestionAnswerGenerateCooldownRedisKey(questionId))

    @Async(AsyncConfig.AnswerGenerateExecutor.NAME)
    @Transactional
    fun generateAnswerAsync(req: AnswerGenerateRequest) {
        setGeneratingStatusAndCooldown(req.questionId)
        try {
            val bot = userRepository.findByUsername(Bot.USERNAME)
                ?: throw EntityNotFoundException(ResponseMessage.User.BOT_NOT_FOUND)
            val question = questionRepository.findById(req.questionId)
                .orElseThrowNotFound { ResponseMessage.Question.NOT_FOUND }

            val res = taskAPIProxy.generateAnswer(req)
            val answer = createAnswer(res, question, bot)
            questionAnswerRepository.save(answer)
        } catch (e: FeignException) {
            // TODO
            e.printStackTrace()
        } finally {
            deleteGeneratingStatus(req.questionId)
        }
    }

    private fun createAnswer(chatResponse: AnswerGenerateResponse, question: Question, bot: User) =
        when (chatResponse.rejectionReason) {
            AnswerGenerateResponse.RejectionReason.NONE ->
                QuestionAnswer(
                    title = chatResponse.title,
                    content = chatResponse.content,
                    question = question,
                    writer = bot
                )

            AnswerGenerateResponse.RejectionReason.OFF_TOPIC,
            AnswerGenerateResponse.RejectionReason.NOT_ENOUGH_INFO ->
                QuestionAnswer(
                    title = REJECTION_TITLE,
                    content = chatResponse.rejectionReason.message,
                    question = question,
                    writer = bot
                )
        }

    private fun setGeneratingStatusAndCooldown(questionId: Long) {
        redisTemplate.opsForValue().set(
            QuestionAnswerGeneratingRedisKey(questionId),
            true.toString(),
            QuestionAnswerGeneratingRedisKey.TTL
        )
        redisTemplate.opsForValue().set(
            QuestionAnswerGenerateCooldownRedisKey(questionId),
            true.toString(),
            QuestionAnswerGenerateCooldownRedisKey.TTL
        )
    }

    private fun deleteGeneratingStatus(questionId: Long) {
        val redisKey = QuestionAnswerGeneratingRedisKey(questionId)
        redisTemplate.delete(redisKey)
    }
}
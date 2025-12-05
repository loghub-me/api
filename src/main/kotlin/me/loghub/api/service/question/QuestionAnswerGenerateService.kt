package me.loghub.api.service.question

import me.loghub.api.config.AsyncConfig
import me.loghub.api.constant.ai.Bot
import me.loghub.api.constant.ai.SystemPrompt
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.message.ServerMessage
import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.dto.task.answer.AnswerGenerateResponse
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.orElseThrowNotFound
import org.springframework.ai.chat.client.ChatClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionAnswerGenerateService(
    private val questionRepository: QuestionRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val chatClient: ChatClient,
    private val userRepository: UserRepository,
) {
    companion object {
        private const val REJECTION_TITLE = "답변이 거절되었습니다"
    }

    fun checkGeneratingAnswer(questionId: Long) =
        redisTemplate.hasKey(RedisKeys.Question.Answer.GENERATING(questionId).key)

    fun checkGenerateCooldown(questionId: Long) =
        redisTemplate.hasKey(RedisKeys.Question.Answer.GENERATE_COOLDOWN(questionId).key)

    @Async(AsyncConfig.AnswerGenerateExecutor.NAME)
    @Transactional
    fun generateAnswerAsync(req: AnswerGenerateRequest) {
        try {
            val bot = userRepository.findByUsername(Bot.USERNAME)
                ?: throw EntityNotFoundException(ResponseMessage.User.BOT_NOT_FOUND)
            val question = questionRepository.findById(req.questionId)
                .orElseThrowNotFound { ResponseMessage.Question.NOT_FOUND }

            val chatOptions = req.chatModel.toChatOptions()
            val chatResponse = chatClient.prompt()
                .options(chatOptions)
                .system(SystemPrompt.ANSWER_GENERATION_BOT)
                .user("# 제목: ${req.questionTitle}\n---\n${req.questionContent}\n---\n${req.userInstruction}")
                .call()
                .entity(AnswerGenerateResponse::class.java)

            checkNotNull(chatResponse) { ServerMessage.FAILED_CALL_CHAT_CLIENT }

            val answer = createAnswer(chatResponse, question, bot)
            questionAnswerRepository.save(answer)
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
        for (redisKey in listOf(
            RedisKeys.Question.Answer.GENERATING(questionId),
            RedisKeys.Question.Answer.GENERATE_COOLDOWN(questionId),
        )) {
            redisTemplate.opsForValue().set(redisKey.key, true.toString(), redisKey.ttl)
        }
    }

    private fun deleteGeneratingStatus(questionId: Long) {
        val redisKey = RedisKeys.Question.Answer.GENERATING(questionId)
        redisTemplate.delete(redisKey.key)
    }
}
package me.loghub.api.worker

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.message.ServerMessage
import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.dto.task.answer.AnswerGenerateResponse
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.orElseThrowNotFound
import org.springframework.ai.chat.client.ChatClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class AnswerGenerateWorker(
    private val chatClient: ChatClient,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private companion object {
        private val queue = ConcurrentLinkedQueue<AnswerGenerateRequest>()
        private val SYSTEM_PROMPT = """
        당신은 개발자 질문 커뮤니티 "LogHub"의 AI 답변 봇이며, 전문적인 프로그래밍 지식을 가지고 있습니다.
        사용자의 질문에 대한 답변을 작성할 때는 다음 규칙을 따르세요.
        
        1. 답변을 제공하지 않아야 하는 경우 : 답변을 작성하지 않고, rejectionReason을 설정하세요.
        1-1. 'OFF_TOPIC' : 질문 내용이 개발, 프로그래밍, 컴퓨터과학에 관련이 없거나, 질문이 아닌 경우
        1-2. 'NOT_ENOUGH_INFO' : 질문이 명확하지 않거나 충분한 정보가 제공되지 않은 경우
        
        2. 답변을 작성해야 하는 경우 : 답변을 작성하고, rejectionReason은 NULL로 설정하세요.
        2-1. title : 평문 형식으로 답변 제목을 작성하세요.
        2-2. content : markdown 형식으로 답변 내용을 작성하세요.
        2-3. 답변은 질문에 대한 정확한 해결책을 제공해야 합니다.
        2-4. 답변의 마지막에는 재치있는 농담 한마디를 추가하세요.
        """.trimIndent()
        private const val BOT_USERNAME = "bot"
        private const val REJECTION_TITLE = "답변이 거절되었습니다"
        private const val CRON = "0/10 * * * * *" // Every 10 seconds
    }

    @Scheduled(cron = CRON)
    @Transactional
    fun process() {
        if (queue.isEmpty()) {
            return
        }

        val bot = userRepository.findByUsername(BOT_USERNAME)
            ?: throw EntityNotFoundException(ResponseMessage.User.BOT_NOT_FOUND)

        while (queue.isNotEmpty()) {
            val req = queue.poll() ?: break
            try {
                generateAnswerAndSave(req, bot)
            } catch (_: Exception) {
                continue
            }
        }
    }

    fun addToQueue(dto: AnswerGenerateRequest) {
        setGeneratingStatusAndCooldown(dto.questionId)
        queue.add(dto)
    }

    private fun generateAnswerAndSave(req: AnswerGenerateRequest, bot: User) {
        val chatOptions = req.chatModel.toChatOptions()
        val chatResponse = chatClient.prompt()
            .options(chatOptions)
            .system(SYSTEM_PROMPT)
            .user("# 제목: ${req.questionTitle}\n---\n${req.questionContent}\n---\n${req.userInstruction}")
            .call()
            .entity(AnswerGenerateResponse::class.java)

        checkNotNull(chatResponse) { ServerMessage.FAILED_CALL_CHAT_CLIENT }

        val question = questionRepository.findById(req.questionId)
            .orElseThrowNotFound { ResponseMessage.Question.NOT_FOUND }
        val answer = createAnswer(chatResponse, question, bot)

        questionAnswerRepository.save(answer)
        deleteGeneratingStatus(req.questionId)
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
        for (key in listOf(
            RedisKey.Question.Answer.GENERATING,
            RedisKey.Question.Answer.GENERATE_COOLDOWN
        )) {
            val redisKey = "${key.prefix}:${questionId}"
            redisTemplate.opsForValue().set(redisKey, true.toString(), key.ttl)
        }
    }

    private fun deleteGeneratingStatus(questionId: Long) {
        val redisKey = "${RedisKey.Question.Answer.GENERATING.prefix}:${questionId}"
        redisTemplate.delete(redisKey)
    }
}
package kr.loghub.api.worker

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.constant.message.ServerMessage
import kr.loghub.api.dto.internal.answer.AnswerGenerateRequest
import kr.loghub.api.dto.internal.answer.AnswerGenerateResponse
import kr.loghub.api.entity.question.Answer
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.question.answer.AnswerRepository
import kr.loghub.api.repository.user.UserRepository
import org.springframework.ai.chat.client.ChatClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class AnswerGenerateWorker(
    private val chatClient: ChatClient,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository
) {
    companion object {
        private val queue = ConcurrentLinkedQueue<AnswerGenerateRequest>()
        private const val BOT_USERNAME = "bot"
        private val SYSTEM_PROMPT = """
        당신은 개발자 질문 커뮤니티의 AI 답변 봇입니다.
        질문에 대한 답변을 작성할 때는 다음 규칙을 따르세요
        
        1. 질문이 개발, 프로그래밍, 컴퓨터 과학에 관련이 없거나, 질문이 아닌 경우에는 answer를 작성하지 말고 rejectionReason를 'off_topic'으로 설정하세요.
        2. 질문이 명확하지 않거나 충분한 정보가 제공되지 않았다면, 답변을 작성하지 말고 rejectionReason를 'not_enough_info'로 설정하세요.
        3. 질문에 대한 답변을 작성할 때는 markdown 형식을 사용하세요.
        4. 당신은 최고의 프로그래머입니다. 답변은 자세하고 구체적으로 작성하세요. (예: 코드 예제, 설명 등)
        5. 답변은 질문에 대한 정확한 해결책을 제공해야 합니다.
        6. 답변의 마지막에는 재치있는 농담 한마디를 추가하세요.
        """.trimIndent()
    }

    @Scheduled(fixedRate = 1000 * 10)  // 10 seconds
    @Transactional
    fun process() {
        if (queue.isEmpty()) {
            return
        }

        val bot = userRepository.findByUsername(BOT_USERNAME)
            ?: throw EntityNotFoundException(ResponseMessage.User.BOT_NOT_FOUND)

        while (queue.isNotEmpty()) {
            val req = queue.poll() ?: break
            generateAnswerAndSave(req, bot)
        }
    }

    fun addToQueue(dto: AnswerGenerateRequest) = queue.add(dto)

    private fun generateAnswerAndSave(req: AnswerGenerateRequest, bot: User) {
        val res = chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user(req.questionContent)
            .call()
            .entity(AnswerGenerateResponse::class.java)

        checkNotNull(res) { ServerMessage.FAILED_CALL_CHAT_CLIENT }

        val answerContent = res.answerContent
        val rejectionReason = res.rejectionReason
        val question = questionRepository.findById(req.questionId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Question.NOT_FOUND) }

        if (rejectionReason != AnswerGenerateResponse.RejectionReason.NONE) {
            val answer = Answer(
                content = rejectionReason.message,
                question = question,
                writer = bot,
            )
            answerRepository.save(answer)
        }

        val answer = Answer(
            content = answerContent,
            question = question,
            writer = bot
        )
        answerRepository.save(answer)
    }
}
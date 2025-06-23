package kr.loghub.api.service.question

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.star.StarRepository
import kr.loghub.api.service.star.StarService
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionStarService(
    private val starRepository: StarRepository,
    private val questionRepository: QuestionRepository,
) : StarService {
    @Transactional(readOnly = true)
    override fun existsStar(questionId: Long, user: User): Boolean =
        starRepository.existsByQuestionIdAndUserId(questionId, user.id!!)

    @Transactional
    override fun addStar(questionId: Long, user: User): Star {
        val question = questionRepository.findById(questionId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Question.NOT_FOUND) }
        checkAlreadyExists(starRepository.existsByQuestionIdAndUserId(questionId, user.id!!)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }
        question.incrementStarCount()
        return starRepository.save(
            Star(user = user, question = question, target = Star.Target.QUESTION)
        )
    }

    @Transactional
    override fun removeStar(questionId: Long, user: User) {
        val affectedRows = starRepository.deleteByQuestionIdAndUserId(questionId, user.id!!)
        checkExists(affectedRows > 0) {
            ResponseMessage.Star.NOT_FOUND
        }
        questionRepository.decrementStarCount(questionId)
    }
}
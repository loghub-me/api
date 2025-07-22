package kr.loghub.api.service.question

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserStar
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.user.UserStarRepository
import kr.loghub.api.service.common.IStarService
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionStarService(
    private val userStarRepository: UserStarRepository,
    private val questionRepository: QuestionRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(questionId: Long, user: User): Boolean =
        userStarRepository.existsByQuestionIdAndUser(questionId, user)

    @Transactional
    override fun addStar(questionId: Long, user: User): UserStar {
        val question = questionRepository.findById(questionId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Question.NOT_FOUND) }

        checkAlreadyExists(userStarRepository.existsByQuestionAndUser(question, user)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }

        question.incrementStarCount()
        return userStarRepository.save(UserStar(user = user, question = question, target = UserStar.Target.QUESTION))
    }

    @Transactional
    override fun removeStar(questionId: Long, user: User) {
        val affectedRows = userStarRepository.deleteByQuestionIdAndUser(questionId, user)

        checkExists(affectedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        questionRepository.decrementStarCount(questionId)
    }
}
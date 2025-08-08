package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.IStarService
import me.loghub.api.util.checkAlreadyExists
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionStarService(
    private val userStarRepository: UserStarRepository,
    private val questionRepository: QuestionRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, user: User): Boolean =
        userStarRepository.existsByQuestionIdAndUser(id, user)

    @Transactional
    override fun addStar(id: Long, user: User): UserStar {
        val question = questionRepository.findById(id)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Question.NOT_FOUND) }

        checkAlreadyExists(userStarRepository.existsByQuestionAndUser(question, user)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }

        question.incrementStarCount()
        return userStarRepository.save(UserStar(user = user, question = question, target = UserStar.Target.QUESTION))
    }

    @Transactional
    override fun removeStar(id: Long, user: User) {
        val affectedRows = userStarRepository.deleteByQuestionIdAndUser(id, user)

        checkExists(affectedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        questionRepository.decrementStarCount(id)
    }
}
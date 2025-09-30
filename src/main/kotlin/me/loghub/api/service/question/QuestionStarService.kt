package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.IStarService
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionStarService(
    private val userStarRepository: UserStarRepository,
    private val questionRepository: QuestionRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, user: User): Boolean {
        val questionRef = questionRepository.getReferenceById(id)
        return userStarRepository.existsByQuestionAndUser(questionRef, user)
    }

    @Transactional
    override fun addStar(id: Long, user: User): UserStar {
        val questionRef = questionRepository.getReferenceById(id)

        checkConflict(
            userStarRepository.existsByQuestionAndUser(questionRef, user)
        ) { ResponseMessage.Star.ALREADY_EXISTS }
        checkExists(
            questionRepository.existsById(id)
        ) { ResponseMessage.Question.NOT_FOUND }

        questionRepository.incrementStarCount(id)
        return userStarRepository.save(UserStar(user = user, question = questionRef, target = UserStar.Target.QUESTION))
    }

    @Transactional
    override fun deleteStar(id: Long, user: User) {
        val questionRef = questionRepository.getReferenceById(id)
        val deletedRows = userStarRepository.deleteByQuestionAndUser(questionRef, user)

        checkExists(deletedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        questionRepository.decrementStarCount(id)
    }
}
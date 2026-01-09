package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.question.QuestionStatsRepository
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
    private val questionStatsRepository: QuestionStatsRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, stargazer: User): Boolean {
        val questionRef = questionRepository.getReferenceById(id)
        return userStarRepository.existsByQuestionAndStargazer(questionRef, stargazer)
    }

    @Transactional
    override fun addStar(id: Long, stargazer: User): UserStar {
        val questionRef = questionRepository.getReferenceById(id)

        checkConflict(
            userStarRepository.existsByQuestionAndStargazer(questionRef, stargazer)
        ) { ResponseMessage.Star.ALREADY_EXISTS }
        checkExists(
            questionRepository.existsById(id)
        ) { ResponseMessage.Question.NOT_FOUND }

        questionStatsRepository.incrementStarCount(id)
        val newStar = UserStar(stargazer = stargazer, question = questionRef, target = UserStar.Target.QUESTION)
        return userStarRepository.save(newStar)
    }

    @Transactional
    override fun deleteStar(id: Long, stargazer: User) {
        val questionRef = questionRepository.getReferenceById(id)
        val deletedRows = userStarRepository.deleteByQuestionAndStargazer(questionRef, stargazer)

        checkExists(deletedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        questionStatsRepository.decrementStarCount(id)
    }
}
package me.loghub.api.service.question

import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.question.QuestionStatsRepository
import me.loghub.api.repository.user.UserStarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionStarServiceTest {
    private lateinit var userStarRepository: UserStarRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionStatsRepository: QuestionStatsRepository

    private lateinit var questionStarService: QuestionStarService

    @BeforeEach
    fun setUp() {
        userStarRepository = mock()
        questionRepository = mock()
        questionStatsRepository = mock()

        questionStarService = QuestionStarService(userStarRepository, questionRepository, questionStatsRepository)
    }

    @Nested
    inner class ExistsStarTest {
        @Test
        fun `should return true when user already starred question`() {
            val questionId = 1L
            val stargazer = QuestionFixtures.writer(id = 10L, username = "stargazer")
            val questionRef = QuestionFixtures.question(id = questionId)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(questionRef)
            whenever(userStarRepository.existsByQuestionAndStargazer(questionRef, stargazer)).thenReturn(true)

            val result = questionStarService.existsStar(questionId, stargazer)

            assertTrue(result)
            verify(questionRepository).getReferenceById(questionId)
            verify(userStarRepository).existsByQuestionAndStargazer(questionRef, stargazer)
        }
    }

    @Nested
    inner class AddStarTest {
        @Test
        fun `should add star when question exists and star does not exist`() {
            val questionId = 1L
            val stargazer = QuestionFixtures.writer(id = 10L, username = "stargazer")
            val questionRef = QuestionFixtures.question(id = questionId)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(questionRef)
            whenever(userStarRepository.existsByQuestionAndStargazer(questionRef, stargazer)).thenReturn(false)
            whenever(questionRepository.existsById(questionId)).thenReturn(true)
            whenever(userStarRepository.save(any<UserStar>())).thenAnswer { invocation ->
                invocation.arguments.first() as UserStar
            }

            val result = questionStarService.addStar(questionId, stargazer)

            assertEquals(UserStar.Target.QUESTION, result.target)
            assertEquals(questionRef, result.question)
            assertEquals(stargazer, result.stargazer)
            verify(questionStatsRepository).incrementStarCount(questionId)
        }

        @Test
        fun `should throw EntityConflictException when star already exists`() {
            val questionId = 1L
            val stargazer = QuestionFixtures.writer(id = 10L, username = "stargazer")
            val questionRef = QuestionFixtures.question(id = questionId)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(questionRef)
            whenever(userStarRepository.existsByQuestionAndStargazer(questionRef, stargazer)).thenReturn(true)

            assertThrows<EntityConflictException> {
                questionStarService.addStar(questionId, stargazer)
            }

            verify(questionRepository, never()).existsById(any())
            verify(questionStatsRepository, never()).incrementStarCount(any())
            verify(userStarRepository, never()).save(any<UserStar>())
        }

        @Test
        fun `should throw EntityNotFoundException when question does not exist`() {
            val questionId = 1L
            val stargazer = QuestionFixtures.writer(id = 10L, username = "stargazer")
            val questionRef = QuestionFixtures.question(id = questionId)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(questionRef)
            whenever(userStarRepository.existsByQuestionAndStargazer(questionRef, stargazer)).thenReturn(false)
            whenever(questionRepository.existsById(questionId)).thenReturn(false)

            assertThrows<EntityNotFoundException> {
                questionStarService.addStar(questionId, stargazer)
            }

            verify(questionStatsRepository, never()).incrementStarCount(any())
            verify(userStarRepository, never()).save(any<UserStar>())
        }
    }

    @Nested
    inner class DeleteStarTest {
        @Test
        fun `should delete star when star exists`() {
            val questionId = 1L
            val stargazer = QuestionFixtures.writer(id = 10L, username = "stargazer")
            val questionRef = QuestionFixtures.question(id = questionId)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(questionRef)
            whenever(userStarRepository.deleteByQuestionAndStargazer(questionRef, stargazer)).thenReturn(1)

            questionStarService.deleteStar(questionId, stargazer)

            verify(userStarRepository).deleteByQuestionAndStargazer(questionRef, stargazer)
            verify(questionStatsRepository).decrementStarCount(questionId)
        }

        @Test
        fun `should throw EntityNotFoundException when star does not exist`() {
            val questionId = 1L
            val stargazer = QuestionFixtures.writer(id = 10L, username = "stargazer")
            val questionRef = QuestionFixtures.question(id = questionId)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(questionRef)
            whenever(userStarRepository.deleteByQuestionAndStargazer(questionRef, stargazer)).thenReturn(0)

            assertThrows<EntityNotFoundException> {
                questionStarService.deleteStar(questionId, stargazer)
            }

            verify(questionStatsRepository, never()).decrementStarCount(any())
        }
    }
}

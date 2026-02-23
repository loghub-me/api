package me.loghub.api.service.user

import me.loghub.api.dto.user.star.UserStarDTO
import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.user.UserStarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class UserStarServiceTest {
    private lateinit var userStarRepository: UserStarRepository

    private lateinit var userStarService: UserStarService

    @BeforeEach
    fun setUp() {
        userStarRepository = mock()
        userStarService = UserStarService(userStarRepository)
    }

    @Nested
    inner class GetStarsTest {
        @Test
        fun `should return paginated user stars when page is valid`() {
            val dto = UserStarDTO(
                id = 1L,
                targetId = 1L,
                slug = "slug",
                title = "title",
                count = 1,
                writer = me.loghub.api.dto.user.UserDTO(1L, "writer"),
                topics = emptyList(),
                target = UserStar.Target.ARTICLE,
            )
            whenever(userStarRepository.findDTOsByStargazerUsername(eq("testuser"), any<Pageable>()))
                .thenReturn(PageImpl(listOf(dto)))

            val result = userStarService.getStars("testuser", 1)

            assertEquals(1, result.content.size)
            assertEquals(dto.id, result.content.first().id)
            verify(userStarRepository).findDTOsByStargazerUsername(eq("testuser"), any<Pageable>())
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive`() {
            assertThrows<IllegalFieldException> {
                userStarService.getStars("testuser", 0)
            }

            verifyNoInteractions(userStarRepository)
        }
    }
}

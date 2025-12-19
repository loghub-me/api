package me.loghub.api.controller.series

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class SeriesChapterControllerTest : BaseControllerTest() {
    object Series {
        object Id {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }
    }

    object SeriesChapter {
        object Sequence {
            const val BY_MEMBER1 = 1
            const val INVALID = 999
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetChapter {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getChapter - not found`() {
            rest.get().uri("/series/${Series.Id.INVALID}/chapters/${SeriesChapter.Sequence.INVALID}")
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getChapter - ok`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .exchange()
                .expectStatus().isOk
                .expectBody<SeriesChapterDetailDTO>()
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetChapterForEdit {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getChapterForEdit - unauthorized`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}/for-edit")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getChapterForEdit - forbidden`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `getChapterForEdit - not found`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.INVALID}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getChapterForEdit - found`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CreateChapter {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `createChapter - unauthorized`() {
            rest.post().uri("/series/${Series.Id.BY_MEMBER1}/chapters")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `createChapter - forbidden`() {
            rest.post().uri("/series/${Series.Id.BY_MEMBER1}/chapters")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `createChapter - not found`() {
            rest.post().uri("/series/${Series.Id.INVALID}/chapters")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `createChapter - created`() {
            rest.post().uri("/series/${Series.Id.BY_MEMBER1}/chapters")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditChapter {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForEdit = EditSeriesChapterDTO(
            title = "Edited Chapter",
            content = "This is an edited chapter.",
            published = true,
        )

        @Test
        fun `editChapter - unauthorized`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editChapter - forbidden`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editChapter - not found`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editChapter - ok`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteChapter {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteChapter - unauthorized`() {
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteChapter - forbidden`() {
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteChapter - not found`() {
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteChapter - ok`() {
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}/chapters/${SeriesChapter.Sequence.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(6)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class UpdateChapterSequence {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdateSequence = UpdateSeriesChapterSequenceDTO(sequences = listOf(2, 1))

        @Test
        fun `updateChapterSequence - unauthorized`() {
            rest.patch().uri("/series/${Series.Id.BY_MEMBER1}/chapters/sequence")
                .body(bodyForUpdateSequence)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `updateChapterSequence - forbidden`() {
            rest.patch().uri("/series/${Series.Id.BY_MEMBER1}/chapters/sequence")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForUpdateSequence)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `updateChapterSequence - bad_request - series not found`() {
            rest.patch().uri("/series/${Series.Id.INVALID}/chapters/sequence")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdateSequence)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `updateChapterSequence - bad_request - duplicate sequences`() {
            val body = UpdateSeriesChapterSequenceDTO(listOf(1, 1))
            rest.patch().uri("/series/${Series.Id.INVALID}/chapters/sequence")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(body)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateChapterSequence - ok`() {
            rest.patch().uri("/series/${Series.Id.BY_MEMBER1}/chapters/sequence")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdateSequence)
                .exchange()
                .expectStatus().isOk
        }
    }
}
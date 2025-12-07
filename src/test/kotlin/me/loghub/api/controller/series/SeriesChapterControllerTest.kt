package me.loghub.api.controller.series

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class SeriesChapterControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    lateinit var member1: User
    lateinit var member1Token: TokenDTO
    lateinit var member2: User
    lateinit var member2Token: TokenDTO

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

    @BeforeAll
    fun setup(@Autowired grantService: TestGrantService) {
        resetDatabase(jdbcTemplate)
        val (member1, member1Token) = grantService.grant("member1")
        this.member1 = member1
        this.member1Token = member1Token
        val (member2, member2Token) = grantService.grant("member2")
        this.member2 = member2
        this.member2Token = member2Token
    }

    private inline fun <reified T> getChapter(seriesId: Long, sequence: Int): ResponseEntity<T> =
        rest.getForEntity("/series/$seriesId/chapters/$sequence", T::class.java)

    private inline fun <reified T> getChapterForEdit(
        id: Long,
        sequence: Int,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.get("/series/$id/chapters/$sequence/for-edit")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> createChapter(seriesId: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/series/$seriesId/chapters")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> editChapter(
        seriesId: Long,
        sequence: Int,
        body: EditSeriesChapterDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/series/$seriesId/chapters/$sequence")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteChapter(
        seriesId: Long,
        sequence: Int,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.delete("/series/$seriesId/chapters/$sequence")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> updateChapterSequence(
        seriesId: Long,
        body: UpdateSeriesChapterSequenceDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.patch("/series/$seriesId/chapters/sequence")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetChapter {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getChapter - not found`() {
            val response = getChapter<String>(Series.Id.INVALID, SeriesChapter.Sequence.INVALID)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getChapter - ok`() {
            val response = getChapter<SeriesChapterDetailDTO>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(SeriesChapter.Sequence.BY_MEMBER1, response.body!!.sequence)
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
            val response = getChapterForEdit<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getChapterForEdit - forbidden`() {
            val response =
                getChapterForEdit<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `getChapterForEdit - not found`() {
            val response = getChapterForEdit<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getChapterForEdit - found`() {
            val response =
                getChapterForEdit<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
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
            val response = createChapter<String>(Series.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `createChapter - forbidden`() {
            val response = createChapter<String>(Series.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `createChapter - not found`() {
            val response = createChapter<String>(Series.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `createChapter - created`() {
            val response = createChapter<String>(Series.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
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
        )

        @Test
        fun `editChapter - unauthorized`() {
            val response = editChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editChapter - forbidden`() {
            val response =
                editChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, bodyForEdit, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editChapter - not found`() {
            val response =
                editChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editChapter - ok`() {
            val response =
                editChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, bodyForEdit, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
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
            val response = deleteChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteChapter - forbidden`() {
            val response = deleteChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteChapter - not found`() {
            val response = deleteChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteChapter - ok`() {
            val response = deleteChapter<String>(Series.Id.BY_MEMBER1, SeriesChapter.Sequence.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
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
            val response = updateChapterSequence<String>(Series.Id.BY_MEMBER1, bodyForUpdateSequence)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `updateChapterSequence - forbidden`() {
            val response = updateChapterSequence<String>(Series.Id.BY_MEMBER1, bodyForUpdateSequence, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `updateChapterSequence - bad_request - series not found`() {
            val response = updateChapterSequence<String>(Series.Id.INVALID, bodyForUpdateSequence, member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `updateChapterSequence - bad_request - duplicate sequences`() {
            val body = UpdateSeriesChapterSequenceDTO(listOf(1, 1))
            val response = updateChapterSequence<String>(Series.Id.INVALID, body, member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateChapterSequence - ok`() {
            val response = updateChapterSequence<String>(Series.Id.BY_MEMBER1, bodyForUpdateSequence, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}
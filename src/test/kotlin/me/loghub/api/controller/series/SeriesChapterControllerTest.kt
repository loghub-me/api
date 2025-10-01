package me.loghub.api.controller.series

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class SeriesChapterControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object SeriesId {
            const val BY_MEMBER1 = 1L
            const val BY_MEMBER2 = 2L
            const val INVALID = 999L
        }

        object SeriesChapterSequence {
            const val BY_MEMBER1 = 1
            const val BY_MEMBER2 = 3
            const val INVALID = 999
        }

        val bodyForEdit = EditSeriesChapterDTO(
            title = "Edited Chapter",
            content = "Edited Chapter Content",
        )
        val bodyForUpdateSequence = UpdateSeriesChapterSequenceDTO(sequences = listOf(2, 1))

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun `getChapter - not found`() {
        val response = getChapter<String>(SeriesId.INVALID, SeriesChapterSequence.INVALID)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getChapter - ok`() {
        val response = getChapter<SeriesChapterDetailDTO>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(SeriesChapterSequence.BY_MEMBER1, response.body!!.sequence)
    }

    @Test
    fun `getChapterForEdit - unauthorized`() {
        val response = getChapterForEdit<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `getChapterForEdit - forbidden`() {
        val response = getChapterForEdit<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `getChapterForEdit - not found`() {
        val response = getChapterForEdit<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getChapterForEdit - found`() {
        val response = getChapterForEdit<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `createChapter - unauthorized`() {
        val response = createChapter<String>(SeriesId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `createChapter - forbidden`() {
        val response = createChapter<String>(SeriesId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `createChapter - not found`() {
        val response = createChapter<String>(SeriesId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `createChapter - created`() {
        val response = createChapter<String>(SeriesId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `editChapter - unauthorized`() {
        val response = editChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, bodyForEdit)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `editChapter - forbidden`() {
        val response = editChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, bodyForEdit, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `editChapter - not found`() {
        val response = editChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.INVALID, bodyForEdit, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `editChapter - ok`() {
        val response = editChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, bodyForEdit, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deleteChapter - unauthorized`() {
        val response = deleteChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteChapter - forbidden`() {
        val response = deleteChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteChapter - not found`() {
        val response = deleteChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteChapter - ok`() {
        val response = deleteChapter<String>(SeriesId.BY_MEMBER1, SeriesChapterSequence.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `updateChapterSequence - unauthorized`() {
        val response = updateChapterSequence<String>(SeriesId.BY_MEMBER1, bodyForUpdateSequence)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `updateChapterSequence - forbidden`() {
        val response = updateChapterSequence<String>(SeriesId.BY_MEMBER1, bodyForUpdateSequence, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `updateChapterSequence - bad request 1`() {
        val response = updateChapterSequence<String>(SeriesId.INVALID, bodyForUpdateSequence, member1)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `updateChapterSequence - bad request 2`() {
        val body = UpdateSeriesChapterSequenceDTO(listOf(1, 1))
        val response = updateChapterSequence<String>(SeriesId.INVALID, body, member1)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `updateChapterSequence - ok`() {
        val response = updateChapterSequence<String>(SeriesId.BY_MEMBER1, bodyForUpdateSequence, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> getChapter(seriesId: Long, sequence: Int): ResponseEntity<T> =
        rest.getForEntity("/series/$seriesId/chapters/$sequence", T::class.java)

    private inline fun <reified T> getChapterForEdit(
        id: Long,
        sequence: Int,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.get("/series/$id/chapters/$sequence/edit")
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
}
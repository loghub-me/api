package me.loghub.api.controller.series

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.series.*
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class SeriesControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object SeriesId {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }

        val bodyForPost = PostSeriesDTO(
            title = "New Series",
            description = "New Series Content",
            thumbnail = "default/series-thumbnail.webp",
            topicSlugs = listOf("java", "spring")
        )
        val bodyForEdit = PostSeriesDTO(
            title = "Edited Series",
            description = "Edited Series Content",
            thumbnail = "default/series-thumbnail.webp",
            topicSlugs = listOf("kotlin", "spring")
        )

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun `searchSeries - default`() {
        val response = searchSeries<Page<SeriesDTO>>("/series")
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `searchSeries - with params`() {
        val uri = UriComponentsBuilder.fromPath("/series")
            .queryParam("query", "series")
            .queryParam("sort", SeriesSort.trending)
            .queryParam("page", 1)
            .toUriString()
        val response = searchSeries<Page<SeriesDTO>>(uri)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `getSeries - not found`() {
        val response = getSeries<String>("member1", "unknown-series")
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getSeries - found`() {
        val response = getSeries<SeriesDetailDTO>("member1", "series-1")
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("series-1", response.body?.slug)
    }

    @Test
    fun `getSeriesForEdit - unauthorized`() {
        val response = getSeriesForEdit<String>(SeriesId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `getSeriesForEdit - forbidden`() {
        val response = getSeriesForEdit<String>(SeriesId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `getSeriesForEdit - not found`() {
        val response = getSeriesForEdit<String>(SeriesId.INVALID, member2)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getSeriesForEdit - found`() {
        val response = getSeriesForEdit<SeriesForEditDTO>(SeriesId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(SeriesId.BY_MEMBER1, response.body?.id)
    }

    @Test
    fun `postSeries - unauthorized`() {
        val response = postSeries<String>(bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `postSeries - created`() {
        val response = postSeries<String>(bodyForPost, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `editSeries - unauthorized`() {
        val response = editSeries<String>(SeriesId.BY_MEMBER1, bodyForEdit)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `editSeries - forbidden`() {
        val response = editSeries<String>(SeriesId.BY_MEMBER1, bodyForEdit, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `editSeries - not found`() {
        val response = editSeries<String>(SeriesId.INVALID, bodyForEdit, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `editSeries - ok`() {
        val response = editSeries<String>(SeriesId.BY_MEMBER1, bodyForEdit, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deleteSeries - unauthorized`() {
        val response = deleteSeries<String>(SeriesId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteSeries - forbidden`() {
        val response = deleteSeries<String>(SeriesId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteSeries - not found`() {
        val response = deleteSeries<String>(SeriesId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteSeries - ok`() {
        val response = deleteSeries<String>(SeriesId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> searchSeries(uri: String) = rest.getForEntity(uri, T::class.java)
    private inline fun <reified T> getSeries(username: String, slug: String) =
        rest.getForEntity("/series/@${username}/${slug}", T::class.java)

    private inline fun <reified T> getSeriesForEdit(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/series/$id/edit")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> postSeries(body: PostSeriesDTO, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/series")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> editSeries(
        id: Long,
        body: PostSeriesDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/series/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteSeries(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/series/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }
}
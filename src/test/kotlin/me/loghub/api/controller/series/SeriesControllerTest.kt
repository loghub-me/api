package me.loghub.api.controller.series

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.series.*
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class SeriesControllerTest(
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

        object Slug {
            const val BY_MEMBER1 = "series-1"
            const val INVALID = "unknown-series"
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

    private inline fun <reified T> searchSeries(uri: String) =
        rest.getForEntity(uri, T::class.java)

    private inline fun <reified T> getSeries(username: String, slug: String) =
        rest.getForEntity("/series/@${username}/${slug}", T::class.java)

    private inline fun <reified T> getSeriesForEdit(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/series/$id/for-edit")
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
        token: TokenDTO? = null,
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

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchSeries {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `searchSeries - ok - no params`() {
            val response = searchSeries<Page<SeriesDTO>>("/series")
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @Test
        fun `searchSeries - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/series")
                .queryParam("query", "series")
                .queryParam("sort", SeriesSort.trending)
                .queryParam("page", 1)
                .toUriString()
            val response = searchSeries<Page<SeriesDTO>>(uri)
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @Test
        fun `searchSeries - bad_request - invalid sort`() {
            val uri = UriComponentsBuilder.fromPath("/series")
                .queryParam("sort", "foo")
                .toUriString()
            val response = searchSeries<String>(uri)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `searchSeries - bad_request - invalid page`() {
            val uri = UriComponentsBuilder.fromPath("/series")
                .queryParam("page", 0)
                .toUriString()
            val response = searchSeries<String>(uri)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetSeries {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getSeries - not_found`() {
            val response = getSeries<String>(member1.username, Series.Slug.INVALID)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getSeries - ok`() {
            val response = getSeries<SeriesDetailDTO>(member1.username, Series.Slug.BY_MEMBER1)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(Series.Slug.BY_MEMBER1, response.body?.slug)
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetSeriesForEdit {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getSeriesForEdit - unauthorized`() {
            val response = getSeriesForEdit<String>(Series.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getSeriesForEdit - forbidden`() {
            val response = getSeriesForEdit<String>(Series.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `getSeriesForEdit - not_found`() {
            val response = getSeriesForEdit<String>(Series.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getSeriesForEdit - ok`() {
            val response = getSeriesForEdit<SeriesForEditDTO>(Series.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(Series.Id.BY_MEMBER1, response.body?.id)
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostSeries {
        val bodyForPost = PostSeriesDTO(
            title = "New Series",
            description = "This is a new series.",
            thumbnail = "0/default-series-thumbnail.webp",
            topicSlugs = listOf("java", "spring"),
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `postSeries - unauthorized`() {
            val response = postSeries<String>(bodyForPost)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `postSeries - bad_request - short title`() {
            val response = postSeries<String>(bodyForPost.copy(title = "f"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postSeries - bad_request - empty description`() {
            val response = postSeries<String>(bodyForPost.copy(description = ""), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postSeries - bad_request - invalid thumbnail`() {
            val response = postSeries<String>(bodyForPost.copy(thumbnail = "foo"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postSeries - created`() {
            val response = postSeries<String>(bodyForPost, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditSeries {
        val bodyForEdit = PostSeriesDTO(
            title = "Edited Series",
            description = "This is an edited series.",
            thumbnail = "0/default-series-thumbnail.webp",
            topicSlugs = listOf("kotlin", "spring-boot"),
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `editSeries - unauthorized`() {
            val response = editSeries<String>(Series.Id.BY_MEMBER1, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editSeries - forbidden`() {
            val response = editSeries<String>(Series.Id.BY_MEMBER1, bodyForEdit, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editSeries - not_found`() {
            val response = editSeries<String>(Series.Id.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editSeries - bad_request - short title`() {
            val response = editSeries<String>(Series.Id.BY_MEMBER1, bodyForEdit.copy(title = "f"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `editSeries - ok`() {
            val response = editSeries<String>(Series.Id.BY_MEMBER1, bodyForEdit, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(6)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteSeries {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteSeries - unauthorized`() {
            val response = deleteSeries<String>(Series.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteSeries - forbidden`() {
            val response = deleteSeries<String>(Series.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteSeries - not_found`() {
            val response = deleteSeries<String>(Series.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteSeries - ok`() {
            val response = deleteSeries<String>(Series.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}
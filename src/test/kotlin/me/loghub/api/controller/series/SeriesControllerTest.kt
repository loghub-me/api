package me.loghub.api.controller.series

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.series.PostSeriesDTO
import me.loghub.api.dto.series.SeriesDetailDTO
import me.loghub.api.dto.series.SeriesForEditDTO
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class SeriesControllerTest : BaseControllerTest() {
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

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchSeries {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `searchSeries - ok - no params`() {
            rest.get().uri("/series")
                .exchange()
                .expectStatus().isOk
        }

        @Test
        fun `searchSeries - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/series")
                .queryParam("query", "series")
                .queryParam("sort", SeriesSort.trending)
                .queryParam("page", 1)
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isOk
        }

        @Test
        fun `searchSeries - bad_request - invalid sort`() {
            val uri = UriComponentsBuilder.fromPath("/series")
                .queryParam("sort", "foo")
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `searchSeries - bad_request - invalid page`() {
            val uri = UriComponentsBuilder.fromPath("/series")
                .queryParam("page", 0)
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isBadRequest
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
            rest.get().uri("/series/@${member1.username}/${Series.Slug.INVALID}")
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getSeries - ok`() {
            rest.get().uri("/series/@${member1.username}/${Series.Slug.BY_MEMBER1}")
                .exchange()
                .expectStatus().isOk
                .expectBody<SeriesDetailDTO>()
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
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/for-edit")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getSeriesForEdit - forbidden`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `getSeriesForEdit - not_found`() {
            rest.get().uri("/series/${Series.Id.INVALID}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getSeriesForEdit - ok`() {
            rest.get().uri("/series/${Series.Id.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody<SeriesForEditDTO>()
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
            rest.post().uri("/series")
                .body(bodyForPost)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `postSeries - bad_request - short title`() {
            rest.post().uri("/series")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(title = "f"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postSeries - bad_request - empty description`() {
            rest.post().uri("/series")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(description = ""))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postSeries - bad_request - invalid thumbnail`() {
            rest.post().uri("/series")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(thumbnail = "foo"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postSeries - created`() {
            rest.post().uri("/series")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isCreated
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
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editSeries - forbidden`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editSeries - not_found`() {
            rest.put().uri("/series/${Series.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editSeries - bad_request - short title`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit.copy(title = "f"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `editSeries - ok`() {
            rest.put().uri("/series/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
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
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteSeries - forbidden`() {
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteSeries - not_found`() {
            rest.delete().uri("/series/${Series.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteSeries - ok`() {
            rest.delete().uri("/series/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}
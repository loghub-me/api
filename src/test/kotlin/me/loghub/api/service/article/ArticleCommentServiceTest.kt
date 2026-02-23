package me.loghub.api.service.article

import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.article.ArticleCommentRepository
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.article.ArticleStatsRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArticleCommentServiceTest {
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleStatsRepository: ArticleStatsRepository
    private lateinit var articleCommentRepository: ArticleCommentRepository

    private lateinit var articleCommentService: ArticleCommentService

    @BeforeEach
    fun setUp() {
        articleRepository = mock()
        articleStatsRepository = mock()
        articleCommentRepository = mock()

        articleCommentService = ArticleCommentService(
            articleRepository,
            articleStatsRepository,
            articleCommentRepository
        )
    }

    @Nested
    inner class GetCommentsTest {
        @Test
        fun `should return root comments when request is valid`() {
            val articleId = 1L
            val articleRef = ArticleFixtures.article(id = articleId)
            val commentWriter = ArticleFixtures.writer(id = 2L, username = "commenter")
            val rootComment = comment(id = 11L, article = articleRef, writer = commentWriter)
            val page = PageImpl(listOf(rootComment))
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(
                articleCommentRepository.findRootComments(
                    articleRef,
                    PageRequest.of(0, 10)
                )
            ).thenReturn(page)

            val result = articleCommentService.getComments(articleId, 1)

            assertEquals(1, result.content.size)
            assertEquals(rootComment.id, result.content.first().id)
            assertEquals(rootComment.content, result.content.first().content)
            verify(articleRepository).getReferenceById(articleId)
            verify(articleCommentRepository).findRootComments(articleRef, PageRequest.of(0, 10))
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive`() {
            assertThrows<IllegalFieldException> {
                articleCommentService.getComments(1L, 0)
            }

            verifyNoInteractions(articleRepository, articleCommentRepository)
        }
    }

    @Nested
    inner class GetRepliesTest {
        @Test
        fun `should return replies when parent comment exists`() {
            val articleId = 1L
            val parentId = 10L
            val articleRef = ArticleFixtures.article(id = articleId)
            val parentRef = comment(id = parentId, article = articleRef)
            val replyWriter = ArticleFixtures.writer(id = 3L, username = "replier")
            val reply = comment(
                id = 12L,
                article = articleRef,
                writer = replyWriter,
                parent = parentRef,
                content = "reply content"
            )
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.getReferenceById(parentId)).thenReturn(parentRef)
            whenever(articleCommentRepository.findLeafComments(articleRef, parentRef)).thenReturn(listOf(reply))

            val result = articleCommentService.getReplies(articleId, parentId)

            assertEquals(1, result.size)
            assertEquals(reply.id, result.first().id)
            assertEquals(reply.content, result.first().content)
            verify(articleRepository).getReferenceById(articleId)
            verify(articleCommentRepository).getReferenceById(parentId)
            verify(articleCommentRepository).findLeafComments(articleRef, parentRef)
        }
    }

    @Nested
    inner class PostCommentTest {
        @Test
        fun `should create root comment when parentId is null`() {
            val articleId = 1L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val article = ArticleFixtures.article(id = articleId)
            val requestBody = PostArticleCommentDTO(content = "  root comment  ", parentId = null)
            val savedComment = comment(id = 100L, article = article, writer = writer, content = "root comment")
            whenever(articleRepository.findWithWriterById(articleId)).thenReturn(article)
            whenever(articleCommentRepository.save(any<ArticleComment>())).thenReturn(savedComment)

            val result = articleCommentService.postComment(articleId, requestBody, writer)

            assertEquals(savedComment.id, result.id)
            verify(articleStatsRepository).incrementCommentCount(articleId)
            verify(articleCommentRepository, never()).incrementReplyCount(any())

            val savedCaptor = argumentCaptor<ArticleComment>()
            verify(articleCommentRepository).save(savedCaptor.capture())
            assertEquals("root comment", savedCaptor.firstValue.content)
            assertNull(savedCaptor.firstValue.parent)
            assertNull(savedCaptor.firstValue.mention)
        }

        @Test
        fun `should create reply and increment parent reply count when parent is root comment`() {
            val articleId = 1L
            val parentId = 20L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val mention = ArticleFixtures.writer(id = 11L, username = "mention")
            val article = ArticleFixtures.article(id = articleId)
            val parent = comment(id = parentId, article = article, writer = mention)
            val requestBody = PostArticleCommentDTO(content = "reply", parentId = parentId)
            val savedComment = comment(
                id = 101L,
                article = article,
                writer = writer,
                parent = parent,
                mention = mention,
                content = "reply"
            )
            whenever(articleRepository.findWithWriterById(articleId)).thenReturn(article)
            whenever(articleCommentRepository.findByArticleAndId(article, parentId)).thenReturn(parent)
            whenever(articleCommentRepository.findWriterById(parentId)).thenReturn(mention)
            whenever(articleCommentRepository.save(any<ArticleComment>())).thenReturn(savedComment)

            val result = articleCommentService.postComment(articleId, requestBody, writer)

            assertEquals(savedComment.id, result.id)
            verify(articleStatsRepository).incrementCommentCount(articleId)
            verify(articleCommentRepository).incrementReplyCount(parentId)

            val savedCaptor = argumentCaptor<ArticleComment>()
            verify(articleCommentRepository).save(savedCaptor.capture())
            assertEquals(parent, savedCaptor.firstValue.parent)
            assertEquals(mention, savedCaptor.firstValue.mention)
        }

        @Test
        fun `should use root parent when replying to nested comment`() {
            val articleId = 1L
            val replyId = 22L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val mention = ArticleFixtures.writer(id = 12L, username = "mention")
            val article = ArticleFixtures.article(id = articleId)
            val rootParent = comment(id = 20L, article = article, writer = ArticleFixtures.writer(id = 13L, username = "root"))
            val nestedReply = comment(id = replyId, article = article, writer = mention, parent = rootParent)
            val requestBody = PostArticleCommentDTO(content = "nested reply", parentId = replyId)
            whenever(articleRepository.findWithWriterById(articleId)).thenReturn(article)
            whenever(articleCommentRepository.findByArticleAndId(article, replyId)).thenReturn(nestedReply)
            whenever(articleCommentRepository.findWriterById(replyId)).thenReturn(mention)
            whenever(articleCommentRepository.save(any<ArticleComment>())).thenAnswer { invocation ->
                invocation.arguments.first() as ArticleComment
            }

            val result = articleCommentService.postComment(articleId, requestBody, writer)

            assertEquals("nested reply", result.content)
            verify(articleStatsRepository).incrementCommentCount(articleId)
            verify(articleCommentRepository).incrementReplyCount(rootParent.id!!)

            val savedCaptor = argumentCaptor<ArticleComment>()
            verify(articleCommentRepository).save(savedCaptor.capture())
            assertEquals(rootParent, savedCaptor.firstValue.parent)
            assertEquals(mention, savedCaptor.firstValue.mention)
        }

        @Test
        fun `should throw EntityNotFoundException when article does not exist`() {
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val requestBody = PostArticleCommentDTO(content = "comment", parentId = null)
            whenever(articleRepository.findWithWriterById(1L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleCommentService.postComment(1L, requestBody, writer)
            }

            verify(articleCommentRepository, never()).save(any<ArticleComment>())
            verify(articleStatsRepository, never()).incrementCommentCount(any())
        }

        @Test
        fun `should throw EntityNotFoundException when parent comment does not exist`() {
            val articleId = 1L
            val parentId = 99L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val article = ArticleFixtures.article(id = articleId)
            val requestBody = PostArticleCommentDTO(content = "reply", parentId = parentId)
            whenever(articleRepository.findWithWriterById(articleId)).thenReturn(article)
            whenever(articleCommentRepository.findByArticleAndId(article, parentId)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleCommentService.postComment(articleId, requestBody, writer)
            }

            verify(articleCommentRepository, never()).findWriterById(any())
            verify(articleCommentRepository, never()).save(any<ArticleComment>())
            verify(articleStatsRepository, never()).incrementCommentCount(any())
        }

        @Test
        fun `should throw IllegalFieldException when parent comment is deleted`() {
            val articleId = 1L
            val parentId = 40L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val article = ArticleFixtures.article(id = articleId)
            val deletedParent = comment(id = parentId, article = article, deleted = true)
            val requestBody = PostArticleCommentDTO(content = "reply", parentId = parentId)
            whenever(articleRepository.findWithWriterById(articleId)).thenReturn(article)
            whenever(articleCommentRepository.findByArticleAndId(article, parentId)).thenReturn(deletedParent)

            assertThrows<IllegalFieldException> {
                articleCommentService.postComment(articleId, requestBody, writer)
            }

            verify(articleCommentRepository, never()).findWriterById(any())
            verify(articleCommentRepository, never()).save(any<ArticleComment>())
            verify(articleStatsRepository, never()).incrementCommentCount(any())
        }
    }

    @Nested
    inner class EditCommentTest {
        @Test
        fun `should edit comment when writer has permission`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val articleRef = ArticleFixtures.article(id = articleId)
            val comment = comment(
                id = commentId,
                article = articleRef,
                writer = writer,
                content = "before edit"
            )
            val requestBody = PostArticleCommentDTO(content = "after\n\n\nedit", parentId = null)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(comment)

            val result = articleCommentService.editComment(articleId, commentId, requestBody, writer)

            assertEquals(comment, result)
            assertEquals("after\n\nedit", result.content)
            verify(articleCommentRepository).findWithGraphByArticleAndId(articleRef, commentId)
        }

        @Test
        fun `should throw EntityNotFoundException when comment does not exist on edit`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val articleRef = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleCommentService.editComment(articleId, commentId, PostArticleCommentDTO("content", null), writer)
            }
        }

        @Test
        fun `should throw PermissionDeniedException when another user edits comment`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val anotherUser = ArticleFixtures.writer(id = 11L, username = "another")
            val articleRef = ArticleFixtures.article(id = articleId)
            val comment = comment(id = commentId, article = articleRef, writer = anotherUser)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(comment)

            assertThrows<PermissionDeniedException> {
                articleCommentService.editComment(articleId, commentId, PostArticleCommentDTO("content", null), writer)
            }
        }
    }

    @Nested
    inner class DeleteCommentTest {
        @Test
        fun `should soft delete comment and decrement reply count when comment is a reply`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val articleRef = ArticleFixtures.article(id = articleId)
            val parent = comment(id = 20L, article = articleRef, writer = writer)
            val reply = comment(id = commentId, article = articleRef, writer = writer, parent = parent)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(reply)

            articleCommentService.deleteComment(articleId, commentId, writer)

            assertTrue(reply.deleted)
            verify(articleCommentRepository).decrementReplyCount(parent.id!!)
            verify(articleStatsRepository).decrementCommentCount(articleId)
        }

        @Test
        fun `should soft delete root comment without decrementing reply count`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val articleRef = ArticleFixtures.article(id = articleId)
            val rootComment = comment(id = commentId, article = articleRef, writer = writer)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(rootComment)

            articleCommentService.deleteComment(articleId, commentId, writer)

            assertTrue(rootComment.deleted)
            verify(articleCommentRepository, never()).decrementReplyCount(any())
            verify(articleStatsRepository).decrementCommentCount(articleId)
        }

        @Test
        fun `should throw EntityNotFoundException when comment does not exist on delete`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val articleRef = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleCommentService.deleteComment(articleId, commentId, writer)
            }

            verify(articleStatsRepository, never()).decrementCommentCount(any())
        }

        @Test
        fun `should throw PermissionDeniedException when another user deletes comment`() {
            val articleId = 1L
            val commentId = 2L
            val writer = ArticleFixtures.writer(id = 10L, username = "writer")
            val anotherUser = ArticleFixtures.writer(id = 11L, username = "another")
            val articleRef = ArticleFixtures.article(id = articleId)
            val comment = comment(id = commentId, article = articleRef, writer = anotherUser)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(articleCommentRepository.findWithGraphByArticleAndId(articleRef, commentId)).thenReturn(comment)

            assertThrows<PermissionDeniedException> {
                articleCommentService.deleteComment(articleId, commentId, writer)
            }

            verify(articleCommentRepository, never()).decrementReplyCount(any())
            verify(articleStatsRepository, never()).decrementCommentCount(any())
        }
    }

    private fun comment(
        id: Long,
        article: Article,
        writer: User = ArticleFixtures.writer(id = id + 100L, username = "writer$id"),
        parent: ArticleComment? = null,
        mention: User? = null,
        content: String = "comment$id",
        deleted: Boolean = false,
        replyCount: Int = 0
    ) = ArticleComment(
        content = content,
        deleted = deleted,
        replyCount = replyCount,
        article = article,
        parent = parent,
        mention = mention,
        writer = writer
    ).apply { this.id = id }
}

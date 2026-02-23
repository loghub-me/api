package me.loghub.api.service.question

import me.loghub.api.constant.ai.LogHubChatModel
import me.loghub.api.dto.question.PostQuestionDTO
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.dto.question.answer.RequestGenerateAnswerDTO
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.question.QuestionStats
import me.loghub.api.entity.user.User
import me.loghub.api.service.article.ArticleFixtures

object QuestionFixtures {
    fun writer(
        id: Long = 1L,
        username: String = "question_writer",
    ) = ArticleFixtures.writer(id = id, username = username)

    fun question(
        id: Long = 1L,
        writer: User = writer(),
        slug: String = "test-question",
        title: String = "Test Question",
        content: String = "Question content",
        normalizedContent: String = "Normalized question content",
        status: Question.Status = Question.Status.OPEN,
    ) = Question(
        slug = slug,
        title = title,
        content = content,
        normalizedContent = normalizedContent,
        status = status,
        stats = QuestionStats(),
        writer = writer,
        writerUsername = writer.username,
        topicsFlat = emptyList(),
    ).apply { this.id = id }

    fun answer(
        id: Long = 1L,
        question: Question = question(),
        writer: User = writer(id = 2L, username = "answerer"),
        title: String = "Answer title",
        content: String = "Answer content",
        accepted: Boolean = false,
    ) = QuestionAnswer(
        title = title,
        content = content,
        accepted = false,
        question = question,
        writer = writer,
    ).apply {
        this.id = id
        if (accepted) {
            this.accept()
        }
    }

    fun postQuestionDTO(
        title: String = "Test Question",
        content: String = "Question content",
        topicSlugs: List<String> = emptyList(),
    ) = PostQuestionDTO(
        title = title,
        content = content,
        topicSlugs = topicSlugs,
    )

    fun postQuestionAnswerDTO(
        title: String = "Answer title",
        content: String = "Answer content",
    ) = PostQuestionAnswerDTO(
        title = title,
        content = content,
    )

    fun requestGenerateAnswerDTO(
        instruction: String? = "Please answer clearly",
        chatModel: LogHubChatModel = LogHubChatModel.GPT_5,
    ) = RequestGenerateAnswerDTO(
        instruction = instruction,
        chatModel = chatModel,
    )
}

package kr.loghub.api.aspect

import kr.loghub.api.constant.redis.RedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class TrendingScoreAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val COMMENT = 1.toDouble()
        const val REVIEW = 1.toDouble()
        const val ANSWER = 2.toDouble()
        const val STAR = 3.toDouble()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* kr.loghub.api.service.article.ArticleCommentService.postComment(..)) && args(articleId, ..))")
    fun updateTrendingScoreAfterPostArticleComment(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), TrendingScoreDelta.COMMENT)

    @AfterReturning("execution(* kr.loghub.api.service.article.ArticleCommentService.removeComment(..)) && args(articleId, ..))")
    fun updateTrendingScoreAfterRemoveArticleComment(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), -TrendingScoreDelta.COMMENT)

    @AfterReturning("execution(* kr.loghub.api.service.article.ArticleStarService.addStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterAddArticleStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* kr.loghub.api.service.article.ArticleStarService.removeStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterRemoveArticleStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), -TrendingScoreDelta.STAR)

    @AfterReturning("execution(* kr.loghub.api.service.book.BookReviewService.postReview(..)) && args(bookId, ..))")
    fun updateTrendingScoreAfterPostBookReview(bookId: Long) =
        zSetOps.incrementScore(RedisKey.Book.TRENDING_SCORE, bookId.toString(), TrendingScoreDelta.REVIEW)

    @AfterReturning("execution(* kr.loghub.api.service.book.BookReviewService.removeReview(..)) && args(bookId, ..))")
    fun updateTrendingScoreAfterRemoveBookReview(bookId: Long) =
        zSetOps.incrementScore(RedisKey.Book.TRENDING_SCORE, bookId.toString(), -TrendingScoreDelta.REVIEW)

    @AfterReturning("execution(* kr.loghub.api.service.book.BookStarService.addStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterAddBookStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Book.TRENDING_SCORE, articleId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* kr.loghub.api.service.book.BookStarService.removeStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterRemoveBookStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Book.TRENDING_SCORE, articleId.toString(), -TrendingScoreDelta.STAR)

    @AfterReturning("execution(* kr.loghub.api.service.question.answer.QuestionAnswerService.postAnswer(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterPostAnswer(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), TrendingScoreDelta.ANSWER)

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionAnswerService.removeAnswer(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterRemoveAnswer(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), -TrendingScoreDelta.ANSWER)

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionStarService.addStar(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterAddQuestionStar(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionStarService.removeStar(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterRemoveQuestionStar(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), -TrendingScoreDelta.STAR)
}
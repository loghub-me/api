package me.loghub.api.repository.article

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.topic.TopicArticleSort
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.QArticle
import me.loghub.api.lib.hibernate.PGroongaHibernateFunction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ArticleCustomRepository(private val entityManager: EntityManager) {
    private companion object {
        val article = QArticle.article;
    }

    fun search(
        query: String,
        sort: ArticleSort,
        pageable: Pageable,
        username: String? = null,
        published: Boolean? = true,
    ): Page<Article> {
        val ftsCondition = query.takeIf { it.isNotBlank() }
            ?.let(::createFullTextSearchCondition)
        val writerCondition = username.takeIf { !it.isNullOrBlank() }
            ?.let(::createWriterCondition)
        val publishedCondition = article.published.eq(published)
        val conditions = listOfNotNull(ftsCondition, writerCondition, publishedCondition).toTypedArray()
        val resolvedSort = sort.takeUnless { ftsCondition == null && it == ArticleSort.relevant }
            ?: ArticleSort.latest

        return runQueryAndWrapPage(conditions = conditions, orders = resolvedSort.orders, pageable = pageable)
    }

    fun findByTopicSlug(
        topicSlug: String,
        sort: TopicArticleSort,
        pageable: Pageable,
        published: Boolean? = true,
    ): Page<Article> {
        val topicCondition = article.topics.any().slug.eq(topicSlug)
        val publishedCondition = article.published.eq(published)
        val conditions = arrayOf(topicCondition, publishedCondition)

        return runQueryAndWrapPage(conditions = conditions, orders = sort.orders, pageable = pageable)
    }

    private fun createWriterCondition(username: String) = article.writerUsername.eq(username)
    private fun createFullTextSearchCondition(query: String) =
        Expressions.booleanTemplate(PGroongaHibernateFunction.ARTICLES_FTS.template, query)

    private fun runQueryAndWrapPage(
        conditions: Array<BooleanExpression>,
        orders: Array<out OrderSpecifier<*>>,
        pageable: Pageable,
    ): Page<Article> {
        val searchQuery = JPAQuery<Article>(entityManager)
            .select(article)
            .from(article)
            .where(*conditions)
            .leftJoin(article.writer).fetchJoin()
            .orderBy(*orders)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
        val countQuery = JPAQuery<Long>(entityManager)
            .select(article.count())
            .from(article)
            .where(*conditions)

        val articles = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(articles, pageable, total)
    }
}
package me.loghub.api.repository.article

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.QArticle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ArticleCustomRepository(private val entityManager: EntityManager) {
    private companion object {
        val article = QArticle.article
    }

    fun search(
        query: String,
        sort: ArticleSort,
        pageable: Pageable,
        username: String? = null
    ): Page<Article> {
        val fullTextSearch = if (query.isNotBlank()) Expressions.booleanTemplate(
            "ecfts({0}, {1})",
            Expressions.constant(query),
            Expressions.constant("articles_search_index")
        ) else null

        val searchQuery = JPAQuery<Article>(entityManager)
            .select(article)
            .from(article)
            .where(
                username?.let { article.writerUsername.eq(it) },
                fullTextSearch
            )
            .orderBy(sort.order)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val countQuery = JPAQuery<Long>(entityManager)
            .select(article.count())
            .from(article)
            .where(
                username?.let { article.writerUsername.eq(it) },
                fullTextSearch
            )

        val articles = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(articles, pageable, total)
    }
}
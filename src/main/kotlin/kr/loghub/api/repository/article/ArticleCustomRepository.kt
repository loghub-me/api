package kr.loghub.api.repository.article

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.article.QArticle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ArticleCustomRepository(private val entityManager: EntityManager) {
    fun search(query: String, sort: ArticleSort, pageable: Pageable): Page<Article> {
        val searchQuery = JPAQuery<Article>(entityManager)
            .select(QArticle.article).from(QArticle.article)
            .orderBy(sort.order)
            .offset(pageable.offset).limit(pageable.pageSize.toLong())
        val countQuery = JPAQuery<Article>(entityManager)
            .select(QArticle.article.count()).from(QArticle.article)

        if (query.isNotBlank()) {
            val fullTextSearch = Expressions.booleanTemplate("ecfts({0})", Expressions.constant(query));
            searchQuery.where(fullTextSearch)
            countQuery.where(fullTextSearch)
        }

        val articles = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(articles, pageable, total)
    }
}
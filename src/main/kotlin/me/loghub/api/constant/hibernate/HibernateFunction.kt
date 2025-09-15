package me.loghub.api.constant.hibernate

import com.querydsl.core.types.dsl.Expressions
import org.hibernate.type.BasicTypeReference
import org.hibernate.type.StandardBasicTypes

enum class HibernateFunction(
    val funName: String,
    val template: String,
    val pattern: String,
    val returnType: BasicTypeReference<*>,
) {
    ECFTS(
        "ecfts",
        "ecfts({0}, {1})",
        """
            ARRAY[title, content, writer_username, topics_flat] &@~ pgroonga_condition(
                pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1),
                weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.CONTENT}, ${WEIGHTS.WRITER_USERNAME}, ${WEIGHTS.TOPICS_FLAT}],
                index_name => ?2
            ) 
        """.trimIndent(),
        StandardBasicTypes.BOOLEAN,
    ),
    PGROONGA_SCORE(
        "pgroonga_score",
        "pgroonga_score({0}, {1})",
        "pgroonga_score(?1, ?2)",
        StandardBasicTypes.DOUBLE,
    );

    private object WEIGHTS {
        const val TITLE = 5
        const val CONTENT = 1
        const val WRITER_USERNAME = 2
        const val TOPICS_FLAT = 2
    }

    object INDEX {
        val ARTICLES_SEARCH_INDEX = Expressions.constant("articles_search_idx")!!
        val SERIES_SEARCH_INDEX = Expressions.constant("series_search_idx")!!
        val QUESTIONS_SEARCH_INDEX = Expressions.constant("questions_search_idx")!!
    }
}
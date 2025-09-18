package me.loghub.api.constant.hibernate

import org.hibernate.type.BasicTypeReference
import org.hibernate.type.StandardBasicTypes

enum class HibernateFunction(
    val funName: String,
    val template: String,
    val pattern: String,
    val returnType: BasicTypeReference<*>,
) {
    ARTICLES_FTS(
        "articles_fts",
        "articles_fts({0})",
        """
            ARRAY[title, content, writer_username, topics_flat] &@~ pgroonga_condition(
                pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1),
                weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.CONTENT}, ${WEIGHTS.WRITER_USERNAME}, ${WEIGHTS.TOPICS_FLAT}],
                index_name => 'articles_fts_idx'
            ) 
        """.trimIndent(),
        StandardBasicTypes.BOOLEAN,
    ),
    SERIES_FTS(
        "series_fts",
        "series_fts({0})",
        """
            ARRAY[title, description, writer_username, topics_flat] &@~ pgroonga_condition(
                pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1),
                weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.DESCRIPTION}, ${WEIGHTS.WRITER_USERNAME}, ${WEIGHTS.TOPICS_FLAT}],
                index_name => 'series_fts_idx'
            ) 
        """.trimIndent(),
        StandardBasicTypes.BOOLEAN,
    ),
    QUESTIONS_FTS(
        "questions_fts",
        "questions_fts({0})",
        """
            ARRAY[title, content, writer_username, topics_flat] &@~ pgroonga_condition(
                pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1),
                weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.CONTENT}, ${WEIGHTS.WRITER_USERNAME}, ${WEIGHTS.TOPICS_FLAT}],
                index_name => 'questions_fts_idx'
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
        const val DESCRIPTION = 1
        const val WRITER_USERNAME = 2
        const val TOPICS_FLAT = 2
    }
}
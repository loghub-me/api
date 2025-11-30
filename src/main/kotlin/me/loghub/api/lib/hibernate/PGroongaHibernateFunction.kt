package me.loghub.api.lib.hibernate

import org.hibernate.type.BasicTypeReference
import org.hibernate.type.StandardBasicTypes

enum class PGroongaHibernateFunction(
    val funName: String,
    val pattern: String,
    val parameterCount: Int,
    val returnType: BasicTypeReference<*>,
) {
    ARTICLES_FTS(
        "articles_fts",
        "ARRAY[title, content, writer_username] &@~ pgroonga_condition(" +
                "pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1)," +
                "weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.CONTENT}, ${WEIGHTS.WRITER_USERNAME}]," +
                "index_name => 'articles_fts_idx')",
        1,
        StandardBasicTypes.BOOLEAN,
    ),
    SERIES_FTS(
        "series_fts",
        "ARRAY[title, description, writer_username] &@~ pgroonga_condition(" +
                "pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1)," +
                "weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.DESCRIPTION}, ${WEIGHTS.WRITER_USERNAME}]," +
                "index_name => 'series_fts_idx')",
        1,
        StandardBasicTypes.BOOLEAN,
    ),
    QUESTIONS_FTS(
        "questions_fts",
        "ARRAY[title, content, writer_username] &@~ pgroonga_condition(" +
                "pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1)," +
                "weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.CONTENT}, ${WEIGHTS.WRITER_USERNAME}]," +
                "index_name => 'questions_fts_idx')",
        1,
        StandardBasicTypes.BOOLEAN,
    ),

    PGROONGA_SCORE(
        "pgroonga_score",
        "pgroonga_score(?1, ?2)",
        2,
        StandardBasicTypes.DOUBLE,
    );

    private object WEIGHTS {
        const val TITLE = 5
        const val CONTENT = 1
        const val DESCRIPTION = 1
        const val WRITER_USERNAME = 2
    }

    val template: String get() = "$funName(${(0 until parameterCount).joinToString(", ") { "{$it}" }})"
}
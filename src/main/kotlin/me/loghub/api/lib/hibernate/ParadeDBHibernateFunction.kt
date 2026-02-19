package me.loghub.api.lib.hibernate

import org.hibernate.type.BasicTypeReference
import org.hibernate.type.StandardBasicTypes

enum class ParadeDBHibernateFunction(
    val funName: String,
    val pattern: String,
    val parameterCount: Int,
    val returnType: BasicTypeReference<*>,
) {
    ARTICLES_FTS(
        "articles_fts",
        "title ||| ?1::pdb.boost(${WEIGHTS.TITLE}) " +
                "OR normalized_content ||| ?1::pdb.boost(${WEIGHTS.NORMALIZED_CONTENT}) " +
                "OR topics_flat ||| ?1::pdb.boost(${WEIGHTS.TOPIC_FLATS})",
        1,
        StandardBasicTypes.BOOLEAN,
    ),
    SERIES_FTS(
        "series_fts",
        "title ||| ?1::pdb.boost(${WEIGHTS.TITLE}) " +
                "OR description ||| ?1::pdb.boost(${WEIGHTS.DESCRIPTION}) " +
                "OR topics_flat ||| ?1::pdb.boost(${WEIGHTS.TOPIC_FLATS})",
        1,
        StandardBasicTypes.BOOLEAN,
    ),
    QUESTIONS_FTS(
        "questions_fts",
        "title ||| ?1::pdb.boost(${WEIGHTS.TITLE}) " +
                "OR normalized_content ||| ?1::pdb.boost(${WEIGHTS.NORMALIZED_CONTENT}) " +
                "OR topics_flat ||| ?1::pdb.boost(${WEIGHTS.TOPIC_FLATS})",
        1,
        StandardBasicTypes.BOOLEAN,
    ),

    SCORE(
        "pdb_score",
        "pdb.score(?1)",
        1,
        StandardBasicTypes.DOUBLE,
    );

    private object WEIGHTS {
        const val TITLE = 5
        const val NORMALIZED_CONTENT = 2
        const val DESCRIPTION = 1
        const val TOPIC_FLATS = 2
    }

    val template: String get() = "$funName(${(0 until parameterCount).joinToString(", ") { "{$it}" }})"
}
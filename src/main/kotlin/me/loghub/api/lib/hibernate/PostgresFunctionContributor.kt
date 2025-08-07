package me.loghub.api.lib.hibernate

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.type.StandardBasicTypes

class PostgresFunctionContributor : FunctionContributor {
    object WEIGHTS {
        const val TITLE = 5
        const val CONTENT = 1
        const val WRITER_USERNAME = 2
        const val TOPICS_FLAT = 2
    }

    override fun contributeFunctions(fc: FunctionContributions) {
        val resolveType = fc.typeConfiguration.basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN)
        fc.functionRegistry.registerPattern(
            "ecfts",
            """
                ARRAY[title, content, writer_username, topics_flat] &@~ pgroonga_condition(
                    pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1),
                    weights => ARRAY[${WEIGHTS.TITLE}, ${WEIGHTS.CONTENT}, ${WEIGHTS.WRITER_USERNAME}, ${WEIGHTS.TOPICS_FLAT}],
                    index_name => ?2
                ) 
            """.trimIndent(),
            resolveType
        )
        fc.functionRegistry.registerPattern(
            "pgroonga_score",
            "pgroonga_score(?1, ?2)",
            resolveType
        )
    }
}
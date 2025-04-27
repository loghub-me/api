package kr.loghub.api.lib.hibernate

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.type.StandardBasicTypes

class PostgresFunctionContributor : FunctionContributor {
    override fun contributeFunctions(fc: FunctionContributions) {
        val resolveType = fc.typeConfiguration.basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN)
        fc.functionRegistry.registerPattern(
            "ecfts",
            "fulltext &@~ pgroonga_query_expand('search_synonyms', 'term', 'synonyms', ?1)",
            resolveType
        )
    }
}
package me.loghub.api.lib.hibernate

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor

class PostgresFunctionContributor : FunctionContributor {
    override fun contributeFunctions(fc: FunctionContributions) {
        PGroongaHibernateFunction.entries.forEach { function ->
            fc.functionRegistry.registerPattern(
                function.funName,
                function.pattern,
                fc.typeConfiguration.basicTypeRegistry.resolve(function.returnType)
            )
        }
    }
}
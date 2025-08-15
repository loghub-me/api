package me.loghub.api.lib.hibernate

import me.loghub.api.constant.hibernate.HibernateFunction
import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor

class PostgresFunctionContributor : FunctionContributor {
    override fun contributeFunctions(fc: FunctionContributions) {
        HibernateFunction.entries.forEach { function ->
            fc.functionRegistry.registerPattern(
                function.funName,
                function.pattern,
                fc.typeConfiguration.basicTypeRegistry.resolve(function.returnType)
            )
        }
    }
}
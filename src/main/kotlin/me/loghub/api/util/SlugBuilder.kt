package me.loghub.api.util

import java.util.*

object SlugBuilder {
    fun generateUniqueSlug(
        slug: String,
        exists: (String) -> Boolean,
    ): String {
        var uniqueSlug = slug
        while (exists(uniqueSlug)) {
            uniqueSlug = "$slug-${UUID.randomUUID()}"
        }
        return uniqueSlug
    }
}
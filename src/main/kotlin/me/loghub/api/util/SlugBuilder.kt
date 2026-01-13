package me.loghub.api.util

import java.security.SecureRandom

object SlugBuilder {
    private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DEFAULT_OTP_LENGTH = 6
    private val secureRandom = SecureRandom()

    fun generateUniqueSlug(
        slug: String,
        exists: (String) -> Boolean,
    ): String {
        var uniqueSlug = slug
        while (exists(uniqueSlug)) {
            uniqueSlug = "$slug-${generateRandomSuffix()}"
        }
        return uniqueSlug
    }

    private fun generateRandomSuffix(length: Int = DEFAULT_OTP_LENGTH): String {
        return (1..length)
            .map { CHARSET[secureRandom.nextInt(CHARSET.length)] }
            .joinToString("")
    }
}
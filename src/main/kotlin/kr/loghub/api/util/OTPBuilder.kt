package kr.loghub.api.util

import java.security.SecureRandom

object OTPBuilder {
    private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val secureRandom = SecureRandom()

    fun generateOTP(length: Int = 6): String {
        return (1..length)
            .map { CHARSET[secureRandom.nextInt(CHARSET.length)] }
            .joinToString("")
    }
}
package kr.loghub.api.util

import java.security.SecureRandom

object OTPBuilder {
    private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DEFAULT_OTP_LENGTH = 6
    private val secureRandom = SecureRandom()

    fun generateOTP(length: Int = DEFAULT_OTP_LENGTH): String {
        return (1..length)
            .map { CHARSET[secureRandom.nextInt(CHARSET.length)] }
            .joinToString("")
    }
}
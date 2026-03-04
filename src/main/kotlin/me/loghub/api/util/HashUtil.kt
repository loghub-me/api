package me.loghub.api.util

import java.security.MessageDigest

private const val SHA_256 = "SHA-256"

fun sha256(input: String): String = MessageDigest
    .getInstance(SHA_256)
    .digest(input.toByteArray())
    .joinToString("") { "%02x".format(it) }

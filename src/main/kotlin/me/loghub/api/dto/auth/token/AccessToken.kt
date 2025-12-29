package me.loghub.api.dto.auth.token

class AccessToken(val value: String) {
    val authorization get() = "Bearer $value"
}
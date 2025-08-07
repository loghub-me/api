package me.loghub.api.constant.validation

object RegexExpression {
    const val USERNAME = "[a-zA-Z0-9]+$"
    const val NICKNAME = "^[a-zA-Z0-9가-힣]+$"
    const val THUMBNAIL = "^(default|[0-9]+)/[a-zA-Z0-9_-]+\\.webp$"
}
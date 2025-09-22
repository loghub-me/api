package me.loghub.api.constant.validation

object RegexExpression {
    const val USERNAME = "[a-zA-Z0-9]+$"
    const val USERNAME_RESERVED =
        "^(?!(?:root|admin|search|post|edit|join|login|logout|legal|safe-link|setting|settings|support|topic|topics)$).*"
    const val NICKNAME = "^[a-zA-Z0-9가-힣]+(?: [a-zA-Z0-9가-힣]+)*$"
    const val THUMBNAIL = "^(default|[0-9]+)/[a-zA-Z0-9_-]+\\.webp$"
}
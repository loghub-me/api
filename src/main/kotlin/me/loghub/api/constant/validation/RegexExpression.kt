package me.loghub.api.constant.validation

object RegexExpression {
    const val USERNAME = "[a-zA-Z0-9]+$"
    const val USERNAME_RESERVED =
        "^(?!(?:root|admin|_not-found|search|post|edit||join|login|logout|legal|safe-link|setting|settings|support|topic|topics|articles|series|questions|notifications)$).*"
    const val NICKNAME = "^[a-zA-Z0-9가-힣]+(?: [a-zA-Z0-9가-힣]+)*$"
    const val THUMBNAIL = "^[0-9]+/[a-zA-Z0-9_-]+\\.webp$"
}
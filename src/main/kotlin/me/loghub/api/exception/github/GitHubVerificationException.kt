package me.loghub.api.exception.github

class GitHubVerificationException(
    override val message: String
) : RuntimeException(message)

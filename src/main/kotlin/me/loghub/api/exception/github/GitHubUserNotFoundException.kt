package me.loghub.api.exception.github

class GitHubUserNotFoundException(
    override val message: String
) : RuntimeException(message)

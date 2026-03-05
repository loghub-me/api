package me.loghub.api.dto.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client")
data class ClientProperties(
    val host: String,
    val domain: String,
)

package me.loghub.api.dto.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "assets")
data class AssetsProperties(
    val host: String,
)

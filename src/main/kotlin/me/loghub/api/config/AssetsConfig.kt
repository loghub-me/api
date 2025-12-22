package me.loghub.api.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "assets")
class AssetsConfig {
    lateinit var host: String

    companion object {
        lateinit var HOST: String
    }

    @PostConstruct
    fun init() {
        Companion.HOST = host
    }
}

package me.loghub.api.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "client")
class ClientConfig {
    lateinit var host: String
    lateinit var domain: String

    companion object {
        lateinit var HOST: String
        lateinit var DOMAIN: String
    }

    @PostConstruct
    fun init() {
        Companion.HOST = host
        Companion.DOMAIN = domain
    }
}

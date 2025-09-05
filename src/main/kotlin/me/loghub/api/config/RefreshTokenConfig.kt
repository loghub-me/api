package me.loghub.api.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.server.Cookie
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Configuration
@ConfigurationProperties(prefix = "refresh-token")
class RefreshTokenConfig {
    lateinit var domain: String

    companion object {
        lateinit var DOMAIN: String
        const val NAME = "refresh_token"
        const val PATH = "/"
        val SAME_SITE = Cookie.SameSite.NONE.name
        val MAX_AGE = 30.days.toJavaDuration()
    }

    @PostConstruct
    fun init() {
        Companion.DOMAIN = domain
    }
}

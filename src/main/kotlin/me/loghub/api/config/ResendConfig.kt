package me.loghub.api.config

import com.resend.Resend
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "resend")
class ResendConfig {
    lateinit var from: String

    companion object {
        lateinit var FROM: String
    }

    @PostConstruct
    fun init() {
        Companion.FROM = from
    }

    @Bean
    fun resendClient(@Value("\${resend.api-key}") apiKey: String): Resend = Resend(apiKey)
}
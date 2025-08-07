package me.loghub.api.config

import com.resend.Resend
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ResendConfig {
    @Bean
    fun resendClient(@Value("\${resend.api-key}") apiKey: String): Resend = Resend(apiKey)
}
package me.loghub.api.config

import com.resend.Resend
import me.loghub.api.dto.config.ResendProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ResendProperties::class)
class ResendConfig(private val props: ResendProperties) {
    @Bean
    fun resendClient() = Resend(props.apiKey)
}
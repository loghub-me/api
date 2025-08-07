package me.loghub.api.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AIConfig {
    @Bean
    fun chatClient(builder: ChatClient.Builder) = builder.build()
}
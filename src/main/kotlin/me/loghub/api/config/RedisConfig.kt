package me.loghub.api.config

import me.loghub.api.dto.auth.token.JoinTokenDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.lib.redis.JoinTokenRedisSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        return template
    }

    @Bean
    fun joinRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, JoinTokenDTO> {
        val template = RedisTemplate<String, JoinTokenDTO>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = JoinTokenRedisSerializer()
        return template
    }

    @Bean
    fun markdownRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, RenderedMarkdownDTO> {
        val template = RedisTemplate<String, RenderedMarkdownDTO>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = Jackson2JsonRedisSerializer(RenderedMarkdownDTO::class.java)
        return template
    }
}
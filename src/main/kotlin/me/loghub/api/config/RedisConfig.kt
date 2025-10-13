package me.loghub.api.config

import me.loghub.api.dto.auth.join.JoinInfoDTO
import me.loghub.api.dto.auth.join.OAuth2JoinInfoDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.lib.redis.JoinInfoRedisSerializer
import me.loghub.api.lib.redis.OAuth2JoinInfoRedisSerializer
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
    fun joinInfoRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, JoinInfoDTO> {
        val template = RedisTemplate<String, JoinInfoDTO>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = JoinInfoRedisSerializer()
        return template
    }

    @Bean
    fun oauth2JoinInfoRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, OAuth2JoinInfoDTO> {
        val template = RedisTemplate<String, OAuth2JoinInfoDTO>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = OAuth2JoinInfoRedisSerializer()
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
package kr.loghub.api.config

import kr.loghub.api.dto.auth.token.JoinTokenDTO
import kr.loghub.api.lib.redis.JoinTokenRedisSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
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
}
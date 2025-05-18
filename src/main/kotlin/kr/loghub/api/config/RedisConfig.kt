package kr.loghub.api.config

import kr.loghub.api.lib.redis.LongRedisSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun refreshTokenTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Long> {
        val template = RedisTemplate<String, Long>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = LongRedisSerializer()
        return template
    }
}
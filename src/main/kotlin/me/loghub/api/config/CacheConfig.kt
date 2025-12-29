package me.loghub.api.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJacksonJsonRedisSerializer(cacheManagerJsonMapper())

        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(1.hours.toJavaDuration())
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfig).build()
    }

    private fun cacheManagerJsonMapper(): ObjectMapper {
        val ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(Any::class.java)
            .build()
        return JsonMapper.builder()
            .findAndAddModules()
            .activateDefaultTyping(
                ptv,
                DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
            )
            .build()
    }
}

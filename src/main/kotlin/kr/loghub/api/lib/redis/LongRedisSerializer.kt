package kr.loghub.api.lib.redis

import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.ByteBuffer

class LongRedisSerializer : RedisSerializer<Long> {
    override fun serialize(value: Long?): ByteArray? =
        value?.let {
            val byteBuffer = ByteBuffer.allocate(8)
            byteBuffer.putLong(it)
            byteBuffer.array()
        }

    override fun deserialize(bytes: ByteArray?): Long? =
        bytes?.let {
            val byteBuffer = ByteBuffer.wrap(it)
            byteBuffer.long
        }
}
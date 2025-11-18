package me.loghub.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class AsyncConfig {
    object MailExecutor {
        const val NAME = "mailExecutor"
        const val CORE_POLL_SIZE = 4
        const val MAX_POOL_SIZE = 8
        const val QUEUE_CAPACITY = 100
        const val THREAD_NAME_PREFIX = "MailExecutor-"
    }

    @Bean(MailExecutor.NAME)
    fun mailExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = MailExecutor.CORE_POLL_SIZE
        maxPoolSize = MailExecutor.MAX_POOL_SIZE
        queueCapacity = MailExecutor.QUEUE_CAPACITY
        setThreadNamePrefix(MailExecutor.THREAD_NAME_PREFIX)
        initialize()
    }
}
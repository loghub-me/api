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

    object AnswerGenerateExecutor {
        const val NAME = "answerGenerateExecutor"
        const val CORE_POLL_SIZE = 2
        const val MAX_POOL_SIZE = 4
        const val QUEUE_CAPACITY = 50
        const val THREAD_NAME_PREFIX = "AnswerGenerateExecutor-"
    }

    @Bean(MailExecutor.NAME)
    fun mailExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = MailExecutor.CORE_POLL_SIZE
        maxPoolSize = MailExecutor.MAX_POOL_SIZE
        queueCapacity = MailExecutor.QUEUE_CAPACITY
        setThreadNamePrefix(MailExecutor.THREAD_NAME_PREFIX)
        initialize()
    }

    @Bean(AnswerGenerateExecutor.NAME)
    fun answerGenerateExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = AnswerGenerateExecutor.CORE_POLL_SIZE
        maxPoolSize = AnswerGenerateExecutor.MAX_POOL_SIZE
        queueCapacity = AnswerGenerateExecutor.QUEUE_CAPACITY
        setThreadNamePrefix(AnswerGenerateExecutor.THREAD_NAME_PREFIX)
        initialize()
    }
}
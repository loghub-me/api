package me.loghub.api.scheduler

import me.loghub.api.entity.user.UserMeta
import me.loghub.api.entity.user.UserStats
import me.loghub.api.repository.user.UserMetaRepository
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class UserMetaJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val userMetaRepository: UserMetaRepository,
) {
    companion object {
        const val JOB_NAME = "userMetaJob"
        const val STEP_NAME = "userMetaStep"
        const val READER_NAME = "userMetaReader"
        const val CHUNK_SIZE = 100
    }

    @Bean(JOB_NAME)
    fun job() = JobBuilder(JOB_NAME, jobRepository)
        .start(step())
        .build()!!

    @Bean(STEP_NAME)
    fun step() = StepBuilder(STEP_NAME, jobRepository)
        .chunk<UserMeta, UserMeta>(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build()!!

    @Bean
    fun reader() = RepositoryItemReaderBuilder<UserMeta>()
        .name(READER_NAME)
        .repository(userMetaRepository)
        .methodName("findAll")
        .sorts(mapOf("user_id" to Sort.Direction.ASC))
        .pageSize(CHUNK_SIZE)
        .build()!!

    @Bean
    fun processor() = ItemProcessor<UserMeta, UserMeta> { meta ->
        val userId = meta.userId ?: return@ItemProcessor meta
        val newStats = UserStats(
            userMetaRepository.countStatsByWriterId(userId),
            userMetaRepository.findTopicUsageTop5ByWriter(userId),
        )

        if (meta.stats == newStats) {
            return@ItemProcessor null
        }

        meta.updateStats(newStats)
        return@ItemProcessor meta
    }

    @Bean
    fun writer() = ItemWriter<UserMeta> { items ->
        userMetaRepository.saveAll(items)
    }
}
package me.loghub.api.scheduler

import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobOperator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserMetaJobScheduler(
    private val jobOperator: JobOperator,
    @Qualifier(UserMetaJobConfig.JOB_NAME) private val job: Job,
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun runUserMetaJob() {
        try {
            val jobParameters = JobParametersBuilder()
                .addString("execTime", LocalDateTime.now().toString())
                .toJobParameters()
            jobOperator.start(job, jobParameters)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
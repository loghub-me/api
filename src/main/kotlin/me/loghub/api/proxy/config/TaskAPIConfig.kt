package me.loghub.api.proxy.config

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class TaskAPIConfig {
    @Bean
    fun taskApiInterceptor(@Value("\${task-api.interval-token}") intervalToken: String) =
        RequestInterceptor { request ->
            request.header(HttpHeaders.AUTHORIZATION, "Bearer $intervalToken")
        }
}
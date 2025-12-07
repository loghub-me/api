package me.loghub.api.proxy.config

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class GitHubAPIConfig {
    @Bean
    fun githubApiInterceptor() =
        RequestInterceptor { request ->
            request.header(HttpHeaders.ACCEPT, "application/vnd.github+json")
            request.header("X-GitHub-Api-Version", "2022-11-28")
        }
}
package me.loghub.api.proxy

import me.loghub.api.dto.task.github.GitHubSocialAccount
import me.loghub.api.dto.task.github.GitHubUserResponse
import me.loghub.api.proxy.config.GitHubAPIConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "github-api",
    url = "\${github-api.host}",
    configuration = [GitHubAPIConfig::class],
)
interface GitHubAPIProxy {
    @GetMapping("/users/{username}")
    fun getUser(@PathVariable username: String): GitHubUserResponse

    @GetMapping("/users/{username}/social_accounts")
    fun getUserSocialAccounts(@PathVariable username: String): List<GitHubSocialAccount>
}
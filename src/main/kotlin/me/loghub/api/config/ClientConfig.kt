package me.loghub.api.config

import jakarta.annotation.PostConstruct
import me.loghub.api.dto.config.ClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class ClientConfig(private val props: ClientProperties) {
    companion object {
        lateinit var HOST: String
        lateinit var DOMAIN: String
    }

    @PostConstruct
    fun init() {
        HOST = props.host
        DOMAIN = props.domain
    }
}

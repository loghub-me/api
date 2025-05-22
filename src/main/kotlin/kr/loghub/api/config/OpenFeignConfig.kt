package kr.loghub.api.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["kr.loghub.api.proxy"])
class OpenFeignConfig {
}
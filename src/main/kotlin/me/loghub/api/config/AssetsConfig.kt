package me.loghub.api.config

import me.loghub.api.dto.config.AssetsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AssetsProperties::class)
class AssetsConfig

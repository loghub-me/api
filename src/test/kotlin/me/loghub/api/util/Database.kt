package me.loghub.api.util

import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator

fun resetDatabase(jdbcTemplate: JdbcTemplate) {
    val dataSource = jdbcTemplate.dataSource
        ?: error("DataSource is required for resetting database")
    val populator = ResourceDatabasePopulator().apply {
        addScript(ClassPathResource("/database/data/truncate.sql"))
        addScript(ClassPathResource("/database/data/test.sql"))
    }

    DatabasePopulatorUtils.execute(populator, dataSource)
}

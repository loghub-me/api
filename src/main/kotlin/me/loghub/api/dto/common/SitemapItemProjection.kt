package me.loghub.api.dto.common

import java.math.BigDecimal

interface SitemapItemProjection {
    val url: String;
    val lastModified: String?;
    val changeFrequency: ChangeFrequency?;
    val priority: BigDecimal?;
    val images: Array<String>?;

    enum class ChangeFrequency { always, hourly, daily, weekly, monthly, yearly, never }
}
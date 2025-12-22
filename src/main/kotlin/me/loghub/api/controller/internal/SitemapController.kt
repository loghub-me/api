package me.loghub.api.controller.internal

import me.loghub.api.dto.common.SitemapItemProjection
import me.loghub.api.service.internal.SitemapService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/sitemap")
class SitemapController(private val sitemapService: SitemapService) {
    @GetMapping
    fun getDynamicSitemap(): ResponseEntity<List<SitemapItemProjection>> {
        val sitemapItems = sitemapService.getDynamicSitemap()
        return ResponseEntity.ok(sitemapItems)
    }
}
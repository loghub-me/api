package me.loghub.api.dto.user.post

import java.time.OffsetDateTime

interface UserPostProjection {
    val title: String;
    val link: String;
    val publishedAt: OffsetDateTime;
}
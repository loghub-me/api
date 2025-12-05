package me.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserPrivacy(
    @Column(name = "email_public", nullable = false)
    val emailPublic: Boolean = false,
)
package me.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserGitHub(
    @Column(name = "github_username", nullable = false, length = 32)
    val username: String? = null,

    @Column(name = "github_verified", nullable = false)
    var verified: Boolean = false,
)
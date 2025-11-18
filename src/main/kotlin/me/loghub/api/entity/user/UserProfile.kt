package me.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserProfile(
    @Column(name = "nickname", nullable = false, length = 12)
    val nickname: String,

    @Column(length = 1024)
    val readme: String = ""
)
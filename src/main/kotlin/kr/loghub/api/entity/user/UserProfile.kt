package kr.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserProfile(
    @Column(name = "nickname", nullable = false, length = 12)
    var nickname: String,

    @Column(length = 512)
    var readme: String = ""
)
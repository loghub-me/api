package kr.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserPrivacy(
    @Column(name = "email_visible", nullable = false)
    var emailVisible: Boolean = false,

    @Column(name = "star_visible", nullable = false)
    var starVisible: Boolean = true
)
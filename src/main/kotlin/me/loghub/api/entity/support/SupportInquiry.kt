package me.loghub.api.entity.support

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import me.loghub.api.entity.PublicEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "support_inquiries")
@EntityListeners(AuditingEntityListener::class)
class SupportInquiry(
    @Column(name = "email", nullable = true, length = 50)
    val email: String?,

    @Column(name = "title", nullable = false, length = 56)
    var title: String,

    @Column(name = "content", nullable = false, length = 8192)
    var content: String,

    @Column(name = "checked", nullable = false)
    val checked: Boolean = false,
) : PublicEntity()
package me.loghub.api.entity.user

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "users_meta")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class UserMeta(
    @Id
    var userId: Long? = null,

    @Embedded
    var profile: UserProfile,

    @Embedded
    var github: UserGitHub,

    @Embedded
    var stats: UserStats,

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    @MapsId
    var user: User? = null,
)

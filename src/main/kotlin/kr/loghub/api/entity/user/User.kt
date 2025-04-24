package kr.loghub.api.entity.user

import jakarta.persistence.*
import kr.loghub.api.entity.PublicEntity
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class User(
    @Column(name = "email", unique = true, nullable = false, length = 255)
    var email: String,

    @Column(name = "username", unique = true, nullable = false, length = 16)
    @get:JvmName("getUsernameProp")  // to prevent JVM signature conflict
    var username: String,

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    val provider: Provider = Provider.LOCAL,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val role: Role = Role.MEMBER,

    @Embedded
    var profile: UserProfile,

    @Embedded
    var privacy: UserPrivacy,

    id: Long = 0L,
) : PublicEntity(id), UserDetails {
    enum class Provider { LOCAL, GOOGLE, GITHUB }
    enum class Role : GrantedAuthority {
        MEMBER, ADMIN, BOT;

        override fun getAuthority(): String = "ROLE_$name"
    }

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(role)

    override fun getUsername(): String = username

    override fun getPassword(): String? = null
}
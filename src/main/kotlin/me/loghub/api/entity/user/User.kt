package me.loghub.api.entity.user

import jakarta.persistence.*
import me.loghub.api.entity.PublicEntity
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
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

    username: String,

    @Column(name = "nickname", nullable = false, length = 12)
    var nickname: String,

    @Column(name = "provider", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val provider: Provider = Provider.LOCAL,

    @Column(name = "role", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val role: Role = Role.MEMBER,

    @Embedded
    var privacy: UserPrivacy,

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    var meta: UserMeta,

    id: Long? = null,
) : PublicEntity(id), UserDetails {
    @Column(name = "username", unique = true, nullable = false, length = 14)
    private var _username = username

    enum class Provider(val registrationId: String) {
        LOCAL("local"),
        GOOGLE("google"),
        GITHUB("github");
    }

    enum class Role : GrantedAuthority {
        MEMBER, ADMIN, BOT;

        override fun getAuthority(): String = "ROLE_$name"
    }

    override fun getUsername(): String = _username

    override fun getPassword(): String? = null

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(role)

    fun updateUsername(username: String) {
        this._username = username
    }

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updateProfile(profile: UserProfile) {
        this.meta.profile = profile
    }

    fun updatePrivacy(privacy: UserPrivacy) {
        this.privacy = privacy
    }

    fun updateGitHub(gitHub: UserGitHub) {
        this.meta.github = gitHub
    }
}
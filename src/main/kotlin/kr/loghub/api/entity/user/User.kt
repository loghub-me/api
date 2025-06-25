package kr.loghub.api.entity.user

import jakarta.persistence.*
import kr.loghub.api.dto.user.UpdateUserPrivacyDTO
import kr.loghub.api.dto.user.UpdateUserProfileDTO
import kr.loghub.api.entity.PublicEntity
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
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

    @Column(name = "provider", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val provider: Provider = Provider.LOCAL,

    @Column(name = "role", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val role: Role = Role.MEMBER,

    @Embedded
    var profile: UserProfile,

    @Embedded
    var privacy: UserPrivacy,

    id: Long? = null,
) : PublicEntity(id), UserDetails {
    enum class Provider { LOCAL, GOOGLE, GITHUB }
    enum class Role : GrantedAuthority {
        MEMBER, ADMIN, BOT;

        override fun getAuthority(): String = "ROLE_$name"
    }

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(role)

    override fun getUsername(): String = username

    override fun getPassword(): String? = null

    fun updateUsername(username: String) {
        this.username = username
    }

    fun updateProfile(requestBody: UpdateUserProfileDTO) {
        this.profile = UserProfile(
            nickname = requestBody.nickname,
            readme = requestBody.readme,
        )
    }

    fun updatePrivacy(requestBody: UpdateUserPrivacyDTO) {
        this.privacy = UserPrivacy(
            emailVisible = requestBody.emailVisible,
            starVisible = requestBody.starVisible,
        )
    }
}
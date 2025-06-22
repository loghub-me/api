package kr.loghub.api.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class PublicEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    override fun equals(other: Any?) = when {
        this === other -> true
        other !is PublicEntity -> false
        this.javaClass != other.javaClass -> false
        id == null || other.id == null -> false
        else -> this.id == other.id
    }

    override fun hashCode() = 31 * javaClass.hashCode() + (id?.hashCode() ?: 0)
}
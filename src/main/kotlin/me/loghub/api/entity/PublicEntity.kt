package me.loghub.api.entity

import jakarta.persistence.*
import me.loghub.api.constant.message.ServerMessage
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class PublicEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
) {
    val persistedId: Long
        get() = id ?: error(ServerMessage.ENTITY_NOT_PERSISTED)

    override fun equals(other: Any?) = when {
        this === other -> true
        other !is PublicEntity -> false
        this.javaClass != other.javaClass -> false
        id == null || other.id == null -> false
        else -> this.id == other.id
    }

    override fun hashCode() = 31 * javaClass.hashCode() + (id?.hashCode() ?: 0)

    @PrePersist
    fun prePersist() {
        val now = OffsetDateTime.now()
        this.createdAt = now
        this.updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        this.updatedAt = OffsetDateTime.now()
    }
}
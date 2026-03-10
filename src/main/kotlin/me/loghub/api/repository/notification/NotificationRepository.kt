package me.loghub.api.repository.notification

import me.loghub.api.entity.notification.Notification
import me.loghub.api.entity.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface NotificationRepository : JpaRepository<Notification, Long> {
    @Query(
        """
        SELECT n
        FROM Notification n
        WHERE n.recipient = :recipient
          AND (:cursor IS NULL OR n.id < :cursor)
        ORDER BY n.id DESC
        LIMIT 50
        """
    )
    @EntityGraph(attributePaths = ["actor"])
    fun findByRecipientLimit20(recipient: User, cursor: Long?): List<Notification>

    @EntityGraph(attributePaths = ["actor"])
    fun findAllByRecipientAndIdGreaterThanOrderByIdAsc(recipient: User, id: Long): List<Notification>

    @EntityGraph(attributePaths = ["recipient"])
    fun findWithRecipientById(id: Long): Notification?

    fun countByRecipientAndReadAtIsNull(recipient: User): Long

    @Modifying
    @Query(
        """
        UPDATE Notification n
        SET n.readAt = :readAt
        WHERE n.recipient.id = :recipientId
          AND n.readAt IS NULL
        """
    )
    fun markAllAsRead(recipientId: Long, readAt: LocalDateTime): Int
}

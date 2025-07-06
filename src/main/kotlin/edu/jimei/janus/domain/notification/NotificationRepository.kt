package edu.jimei.janus.domain.notification

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByRecipientIdOrderByCreatedAtDesc(recipientId: UUID): List<Notification>
    
    fun findByRecipientIdAndIsReadOrderByCreatedAtDesc(recipientId: UUID, isRead: Boolean): List<Notification>
    
    fun findByType(type: NotificationType): List<Notification>
    
    fun findByRecipientIdAndType(recipientId: UUID, type: NotificationType): List<Notification>
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :recipientId AND n.isRead = false")
    fun countUnreadByRecipientId(@Param("recipientId") recipientId: UUID): Long
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.recipient.id = :recipientId AND n.isRead = false")
    fun markAllAsReadByRecipientId(@Param("recipientId") recipientId: UUID, @Param("readAt") readAt: LocalDateTime)
    
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    fun deleteOldNotifications(@Param("cutoffDate") cutoffDate: LocalDateTime)
}

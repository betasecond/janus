package edu.jimei.janus.controller.dto

import edu.jimei.janus.domain.notification.Notification
import edu.jimei.janus.domain.notification.NotificationType
import java.time.LocalDateTime
import java.util.UUID

data class CreateNotificationDto(
    val title: String,
    val content: String,
    val type: NotificationType,
    val recipientId: UUID,
    val senderId: UUID?
)

data class BroadcastNotificationDto(
    val title: String,
    val content: String,
    val role: String,
    val type: NotificationType,
    val senderId: UUID?
)

data class NotificationDto(
    val id: UUID,
    val title: String,
    val content: String,
    val type: NotificationType,
    val sender: UserDto?,
    val recipient: UserDto,
    val isRead: Boolean,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime?
)

data class NotificationSummaryDto(
    val totalCount: Long,
    val unreadCount: Long,
    val byType: Map<NotificationType, Long>
)

data class MarkReadDto(
    val notificationIds: List<UUID>
)

// 扩展函数：Domain对象转DTO
fun Notification.toDto(): NotificationDto {
    return NotificationDto(
        id = this.id ?: throw IllegalArgumentException("Notification id cannot be null"),
        title = this.title,
        content = this.content,
        type = this.type,
        sender = this.sender?.toDto(),
        recipient = this.recipient.toDto(),
        isRead = this.isRead,
        readAt = this.readAt,
        createdAt = this.createdAt
    )
}

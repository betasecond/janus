package edu.jimei.janus.controller.vo

import edu.jimei.janus.controller.vo.common.UserVO
import edu.jimei.janus.controller.vo.common.toVo
import edu.jimei.janus.domain.notification.Notification
import edu.jimei.janus.domain.notification.NotificationType
import java.time.LocalDateTime
import java.util.UUID

data class NotificationVO(
    val id: UUID,
    val title: String,
    val content: String,
    val type: NotificationType,
    val sender: UserVO?,
    val recipient: UserVO,
    val isRead: Boolean,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime?
)

fun Notification.toVo(): NotificationVO {
    return NotificationVO(
        id = this.id ?: throw IllegalArgumentException("Notification id cannot be null"),
        title = this.title,
        content = this.content,
        type = this.type,
        sender = this.sender?.toVo(),
        recipient = this.recipient.toVo(),
        isRead = this.isRead,
        readAt = this.readAt,
        createdAt = this.createdAt
    )
} 
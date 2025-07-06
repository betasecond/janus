package edu.jimei.janus.application.service

import edu.jimei.janus.domain.notification.Notification
import edu.jimei.janus.domain.notification.NotificationRepository
import edu.jimei.janus.domain.notification.NotificationType
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {

    fun sendNotification(
        title: String,
        content: String,
        type: NotificationType,
        recipientId: UUID,
        senderId: UUID? = null
    ): Notification {
        val recipient = userRepository.findById(recipientId).orElseThrow {
            IllegalArgumentException("Recipient with ID $recipientId not found")
        }

        val sender = senderId?.let { id ->
            userRepository.findById(id).orElseThrow {
                IllegalArgumentException("Sender with ID $id not found")
            }
        }

        val notification = Notification(
            title = title,
            content = content,
            type = type,
            recipient = recipient,
            sender = sender
        )

        return notificationRepository.save(notification)
    }

    fun broadcastToRole(
        title: String,
        content: String,
        role: String,
        type: NotificationType,
        senderId: UUID? = null
    ): List<Notification> {
        val users = userRepository.findAll().filter { it.role == role }
        if (users.isEmpty()) {
            return emptyList()
        }

        val sender = senderId?.let { id ->
            userRepository.findById(id).orElseThrow {
                IllegalArgumentException("Sender with ID $id not found")
            }
        }

        val notifications = users.map { user ->
            Notification(
                title = title,
                content = content,
                type = type,
                recipient = user,
                sender = sender
            )
        }

        return notificationRepository.saveAll(notifications)
    }

    fun getNotificationsByRecipient(recipientId: UUID): List<Notification> {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
    }

    fun getUnreadNotifications(recipientId: UUID): List<Notification> {
        return notificationRepository.findByRecipientIdAndIsReadOrderByCreatedAtDesc(recipientId, false)
    }

    fun getUnreadCount(recipientId: UUID): Long {
        return notificationRepository.countUnreadByRecipientId(recipientId)
    }

    fun markAsRead(notificationId: UUID, recipientId: UUID): Notification {
        val notification = notificationRepository.findById(notificationId).orElseThrow {
            IllegalArgumentException("Notification with ID $notificationId not found")
        }

        if (notification.recipient.id != recipientId) {
            throw IllegalArgumentException("Notification does not belong to recipient")
        }

        notification.markAsRead()
        return notificationRepository.save(notification)
    }

    fun markAllAsRead(recipientId: UUID) {
        notificationRepository.markAllAsReadByRecipientId(recipientId, LocalDateTime.now())
    }

    fun deleteNotification(notificationId: UUID, recipientId: UUID) {
        val notification = notificationRepository.findById(notificationId).orElseThrow {
            IllegalArgumentException("Notification with ID $notificationId not found")
        }

        if (notification.recipient.id != recipientId) {
            throw IllegalArgumentException("Notification does not belong to recipient")
        }

        notificationRepository.delete(notification)
    }

    fun getNotificationsByType(type: NotificationType): List<Notification> {
        return notificationRepository.findByType(type)
    }

    fun getNotificationsByRecipientAndType(recipientId: UUID, type: NotificationType): List<Notification> {
        return notificationRepository.findByRecipientIdAndType(recipientId, type)
    }

    @Transactional
    fun deleteOldNotifications(daysOld: Int) {
        val cutoffDate = LocalDateTime.now().minusDays(daysOld.toLong())
        notificationRepository.deleteOldNotifications(cutoffDate)
    }

    // 便利方法：发送作业相关通知
    fun notifyAssignmentCreated(assignmentTitle: String, courseId: UUID, teacherId: UUID) {
        // 这里可以获取课程的所有学生并发送通知
        // 简化实现，实际应该查询课程的学生列表
        val title = "新作业发布"
        val content = "教师发布了新作业：$assignmentTitle"
        
        // TODO: 实现向课程所有学生发送通知的逻辑
    }    // 便利方法：发送成绩通知
    fun notifyGradePublished(assignmentTitle: String, score: Double, studentId: UUID, teacherId: UUID): Notification {
        val title = "作业已批改"
        val content = "您的作业《$assignmentTitle》已批改完成，得分：$score"
        
        return sendNotification(
            title = title,
            content = content,
            type = NotificationType.GRADE,
            recipientId = studentId,
            senderId = teacherId
        )
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Notification> {
        return notificationRepository.findAll()
    }
}

package edu.jimei.janus.application.service

import edu.jimei.janus.domain.notification.Notification
import edu.jimei.janus.domain.notification.NotificationRepository
import edu.jimei.janus.domain.notification.NotificationType
import edu.jimei.janus.domain.user.UserRepository
import edu.jimei.janus.domain.course.CourseRepository
import edu.jimei.janus.domain.assignment.AssignmentRepository
import edu.jimei.janus.domain.assignment.AssignmentSubmissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: AssignmentSubmissionRepository
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

    fun markMultipleAsRead(notificationIds: List<UUID>, recipientId: UUID) {
        if (notificationIds.isEmpty()) {
            return
        }
        notificationRepository.markAsReadByIdsAndRecipientId(notificationIds, recipientId, LocalDateTime.now())
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
    fun notifyAssignmentCreated(assignmentId: UUID, courseId: UUID, teacherId: UUID) {
        val assignment = assignmentRepository.findById(assignmentId).orElseThrow {
            IllegalArgumentException("Assignment with ID $assignmentId not found")
        }
        val course = courseRepository.findById(courseId).orElseThrow {
            IllegalArgumentException("Course with ID $courseId not found")
        }
        val sender = userRepository.findById(teacherId).orElseThrow {
            IllegalArgumentException("Teacher with ID $teacherId not found")
        }

        val title = "新作业发布"
        val content = "课程《${course.name}》发布了新作业：${assignment.title}"

        val notifications = course.students.map { student ->
            Notification(
                title = title,
                content = content,
                type = NotificationType.ASSIGNMENT,
                recipient = student,
                sender = sender
            )
        }

        if (notifications.isNotEmpty()) {
            notificationRepository.saveAll(notifications)
        }
    }

    // 便利方法：发送成绩通知
    fun notifyGradePublished(submissionId: UUID, score: BigDecimal, studentId: UUID, teacherId: UUID): Notification {
        val submission = submissionRepository.findById(submissionId).orElseThrow {
            IllegalArgumentException("Submission with ID $submissionId not found")
        }
        val assignmentTitle = submission.assignment.title
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

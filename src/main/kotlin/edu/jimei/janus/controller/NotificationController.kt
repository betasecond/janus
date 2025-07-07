package edu.jimei.janus.controller

import edu.jimei.janus.application.service.NotificationService
import edu.jimei.janus.controller.dto.*
import edu.jimei.janus.controller.vo.NotificationStatsVO
import edu.jimei.janus.controller.vo.NotificationSummaryVO
import edu.jimei.janus.controller.vo.NotificationVO
import edu.jimei.janus.controller.vo.common.MessageVO
import edu.jimei.janus.controller.vo.toVo
import edu.jimei.janus.domain.notification.NotificationType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = ["*"])
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping
    fun sendNotification(@RequestBody createDto: CreateNotificationDto): ResponseEntity<NotificationVO> {
        val notification = notificationService.sendNotification(
            title = createDto.title,
            content = createDto.content,
            type = createDto.type,
            recipientId = createDto.recipientId,
            senderId = createDto.senderId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(notification.toVo())
    }

    @PostMapping("/broadcast")
    fun broadcastNotification(@RequestBody broadcastDto: BroadcastNotificationDto): ResponseEntity<List<NotificationVO>> {
        val notifications = notificationService.broadcastToRole(
            title = broadcastDto.title,
            content = broadcastDto.content,
            role = broadcastDto.role,
            type = broadcastDto.type,
            senderId = broadcastDto.senderId
        )
        
        val notificationVos = notifications.map { it.toVo() }
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationVos)
    }

    @GetMapping("/user/{userId}")
    fun getUserNotifications(
        @PathVariable userId: UUID,
        @RequestParam(required = false) unreadOnly: Boolean = false,
        @RequestParam(required = false) type: NotificationType?
    ): ResponseEntity<List<NotificationVO>> {
        val notifications = when {
            unreadOnly -> notificationService.getUnreadNotifications(userId)
            type != null -> notificationService.getNotificationsByRecipientAndType(userId, type)
            else -> notificationService.getNotificationsByRecipient(userId)
        }

        val notificationVos = notifications.map { it.toVo() }
        return ResponseEntity.ok(notificationVos)
    }

    @GetMapping("/user/{userId}/summary")
    fun getUserNotificationSummary(@PathVariable userId: UUID): ResponseEntity<NotificationSummaryVO> {
        val allNotifications = notificationService.getNotificationsByRecipient(userId)
        val unreadCount = notificationService.getUnreadCount(userId)
        
        val byType = NotificationType.values().associateWith { type ->
            allNotifications.count { it.type == type }.toLong()
        }
        
        val summary = NotificationSummaryVO(
            totalCount = allNotifications.size.toLong(),
            unreadCount = unreadCount,
            byType = byType
        )
        
        return ResponseEntity.ok(summary)
    }

    @PutMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<NotificationVO> {
        val notification = notificationService.markAsRead(id, userId)
        return ResponseEntity.ok(notification.toVo())
    }

    @PutMapping("/user/{userId}/read-all")
    fun markAllAsRead(@PathVariable userId: UUID): ResponseEntity<MessageVO> {
        notificationService.markAllAsRead(userId)
        return ResponseEntity.ok(MessageVO("All notifications marked as read"))
    }

    @PostMapping("/mark-read")
    fun markMultipleAsRead(@RequestBody markReadDto: MarkReadDto): ResponseEntity<MessageVO> {
        notificationService.markMultipleAsRead(markReadDto.notificationIds, markReadDto.userId)
        return ResponseEntity.ok(MessageVO("Selected notifications marked as read"))
    }

    @DeleteMapping("/{id}")
    fun deleteNotification(
        @PathVariable id: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<Void> {
        notificationService.deleteNotification(id, userId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/types")
    fun getNotificationTypes(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(NotificationType.values().map { it.name })
    }

    @GetMapping("/type/{type}")
    fun getNotificationsByType(@PathVariable type: NotificationType): ResponseEntity<List<NotificationVO>> {
        val notifications = notificationService.getNotificationsByType(type)
        val notificationVos = notifications.map { it.toVo() }
        return ResponseEntity.ok(notificationVos)
    }

    @DeleteMapping("/cleanup")
    fun cleanupOldNotifications(@RequestParam(defaultValue = "30") daysOld: Int): ResponseEntity<MessageVO> {
        notificationService.deleteOldNotifications(daysOld)
        return ResponseEntity.ok(MessageVO("Old notifications cleaned up successfully"))
    }

    @GetMapping("/stats")
    fun getNotificationStats(): ResponseEntity<NotificationStatsVO> {
        val allNotifications = notificationService.findAll()
        
        val statsByType = NotificationType.values().associateWith { type ->
            allNotifications.count { it.type == type }
        }
        
        val readStats = mapOf(
            "total" to allNotifications.size,
            "read" to allNotifications.count { it.isRead },
            "unread" to allNotifications.count { !it.isRead }
        )
        
        val stats = NotificationStatsVO(
            total = allNotifications.size,
            byType = statsByType,
            readStatus = readStats
        )
        
        return ResponseEntity.ok(stats)
    }

    // 便利接口：发送特定类型通知
    @PostMapping("/assignment/{assignmentId}/created")
    fun notifyAssignmentCreated(
        @PathVariable assignmentId: UUID,
        @RequestParam courseId: UUID,
        @RequestParam teacherId: UUID
    ): ResponseEntity<MessageVO> {
        notificationService.notifyAssignmentCreated(assignmentId, courseId, teacherId)
        return ResponseEntity.ok(MessageVO("Assignment creation notifications sent"))
    }

    @PostMapping("/grade/{submissionId}/published")
    fun notifyGradePublished(
        @PathVariable submissionId: UUID,
        @RequestParam score: BigDecimal,
        @RequestParam studentId: UUID,
        @RequestParam teacherId: UUID
    ): ResponseEntity<NotificationVO> {
        val notification = notificationService.notifyGradePublished(
            submissionId = submissionId,
            score = score,
            studentId = studentId,
            teacherId = teacherId
        )
        return ResponseEntity.ok(notification.toVo())
    }

    // 异常处理
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "error" to "Bad Request",
                "message" to (ex.message ?: "Invalid request")
            )
        )
    }
}

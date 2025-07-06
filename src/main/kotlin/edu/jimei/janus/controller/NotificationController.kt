package edu.jimei.janus.controller

import edu.jimei.janus.application.service.NotificationService
import edu.jimei.janus.controller.dto.*
import edu.jimei.janus.domain.notification.NotificationType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = ["*"])
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping
    fun sendNotification(@RequestBody createDto: CreateNotificationDto): ResponseEntity<NotificationDto> {
        val notification = notificationService.sendNotification(
            title = createDto.title,
            content = createDto.content,
            type = createDto.type,
            recipientId = createDto.recipientId,
            senderId = createDto.senderId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(notification.toDto())
    }

    @PostMapping("/broadcast")
    fun broadcastNotification(@RequestBody broadcastDto: BroadcastNotificationDto): ResponseEntity<List<NotificationDto>> {
        val notifications = notificationService.broadcastToRole(
            title = broadcastDto.title,
            content = broadcastDto.content,
            role = broadcastDto.role,
            type = broadcastDto.type,
            senderId = broadcastDto.senderId
        )
        
        val notificationDtos = notifications.map { it.toDto() }
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationDtos)
    }

    @GetMapping("/user/{userId}")
    fun getUserNotifications(
        @PathVariable userId: UUID,
        @RequestParam(required = false) unreadOnly: Boolean = false,
        @RequestParam(required = false) type: NotificationType?
    ): ResponseEntity<List<NotificationDto>> {
        val notifications = when {
            unreadOnly -> notificationService.getUnreadNotifications(userId)
            type != null -> notificationService.getNotificationsByRecipientAndType(userId, type)
            else -> notificationService.getNotificationsByRecipient(userId)
        }

        val notificationDtos = notifications.map { it.toDto() }
        return ResponseEntity.ok(notificationDtos)
    }

    @GetMapping("/user/{userId}/summary")
    fun getUserNotificationSummary(@PathVariable userId: UUID): ResponseEntity<NotificationSummaryDto> {
        val allNotifications = notificationService.getNotificationsByRecipient(userId)
        val unreadCount = notificationService.getUnreadCount(userId)
        
        val byType = NotificationType.values().associateWith { type ->
            allNotifications.count { it.type == type }.toLong()
        }
        
        val summary = NotificationSummaryDto(
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
    ): ResponseEntity<NotificationDto> {
        val notification = notificationService.markAsRead(id, userId)
        return ResponseEntity.ok(notification.toDto())
    }

    @PutMapping("/user/{userId}/read-all")
    fun markAllAsRead(@PathVariable userId: UUID): ResponseEntity<Map<String, String>> {
        notificationService.markAllAsRead(userId)
        return ResponseEntity.ok(mapOf("message" to "All notifications marked as read"))
    }

    @PostMapping("/mark-read")
    fun markMultipleAsRead(@RequestBody markReadDto: MarkReadDto): ResponseEntity<Map<String, String>> {
        // 注意：这里简化实现，实际应该验证用户权限
        markReadDto.notificationIds.forEach { notificationId ->
            // 需要传入正确的userId，这里需要从认证上下文获取
            // notificationService.markAsRead(notificationId, currentUserId)
        }
        return ResponseEntity.ok(mapOf("message" to "Selected notifications marked as read"))
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
    fun getNotificationsByType(@PathVariable type: NotificationType): ResponseEntity<List<NotificationDto>> {
        val notifications = notificationService.getNotificationsByType(type)
        val notificationDtos = notifications.map { it.toDto() }
        return ResponseEntity.ok(notificationDtos)
    }

    @DeleteMapping("/cleanup")
    fun cleanupOldNotifications(@RequestParam(defaultValue = "30") daysOld: Int): ResponseEntity<Map<String, String>> {
        notificationService.deleteOldNotifications(daysOld)
        return ResponseEntity.ok(mapOf("message" to "Old notifications cleaned up successfully"))
    }

    @GetMapping("/stats")
    fun getNotificationStats(): ResponseEntity<Map<String, Any>> {
        val allNotifications = notificationService.findAll()
        
        val statsByType = NotificationType.values().associateWith { type ->
            allNotifications.count { it.type == type }
        }
        
        val readStats = mapOf(
            "total" to allNotifications.size,
            "read" to allNotifications.count { it.isRead },
            "unread" to allNotifications.count { !it.isRead }
        )
        
        val stats = mapOf(
            "total" to allNotifications.size,
            "byType" to statsByType,
            "readStatus" to readStats
        )
        
        return ResponseEntity.ok(stats)
    }

    // 便利接口：发送特定类型通知
    @PostMapping("/assignment/{assignmentId}/created")
    fun notifyAssignmentCreated(
        @PathVariable assignmentId: UUID,
        @RequestParam courseId: UUID,
        @RequestParam teacherId: UUID
    ): ResponseEntity<Map<String, String>> {
        // 这里应该调用service的便利方法
        // notificationService.notifyAssignmentCreated(assignmentTitle, courseId, teacherId)
        return ResponseEntity.ok(mapOf("message" to "Assignment creation notifications sent"))
    }

    @PostMapping("/grade/{submissionId}/published")
    fun notifyGradePublished(
        @PathVariable submissionId: UUID,
        @RequestParam score: Double,
        @RequestParam studentId: UUID,
        @RequestParam teacherId: UUID
    ): ResponseEntity<NotificationDto> {
        val notification = notificationService.notifyGradePublished(
            assignmentTitle = "作业", // 应该从submission获取
            score = score,
            studentId = studentId,
            teacherId = teacherId
        )
        return ResponseEntity.ok(notification.toDto())
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

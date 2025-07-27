package edu.jimei.janus.controller

import edu.jimei.janus.application.service.NotificationService
import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.controller.dto.*
import edu.jimei.janus.controller.mapper.NotificationVOMapper
import edu.jimei.janus.controller.vo.NotificationStatsVO
import edu.jimei.janus.controller.vo.NotificationSummaryVO
import edu.jimei.janus.controller.vo.NotificationVO
import edu.jimei.janus.controller.vo.common.MessageVO
import edu.jimei.janus.domain.notification.NotificationType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
    private val notificationVOMapper: NotificationVOMapper
) {

    /**
     * Retrieves all notifications.
     *
     * NOTE: This is a general-purpose endpoint for demonstration, mirroring the
     * frontend mock API. In a production environment, you would typically want
     * to paginate this result and apply security constraints.
     */
    @GetMapping
    fun getAllNotifications(): ResponseEntity<ApiResponse<List<NotificationVO>>> {
        val notifications = notificationService.findAll()
        val notificationVos = notifications.map { notificationVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = notificationVos))
    }

    @PostMapping
    fun sendNotification(@RequestBody createDto: CreateNotificationDto): ResponseEntity<ApiResponse<NotificationVO>> {
        val notification = notificationService.sendNotification(
            title = createDto.title,
            content = createDto.content,
            type = createDto.type,
            recipientId = createDto.recipientId,
            senderId = createDto.senderId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = notificationVOMapper.toVO(notification)))
    }

    @PostMapping("/broadcast")
    fun broadcastNotification(@RequestBody broadcastDto: BroadcastNotificationDto): ResponseEntity<ApiResponse<List<NotificationVO>>> {
        val notifications = notificationService.broadcastToRole(
            title = broadcastDto.title,
            content = broadcastDto.content,
            role = broadcastDto.role,
            type = broadcastDto.type,
            senderId = broadcastDto.senderId
        )
        
        val notificationVos = notifications.map { notificationVOMapper.toVO(it) }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = notificationVos))
    }

    @GetMapping("/user/{userId}")
    fun getUserNotifications(
        @PathVariable userId: UUID,
        @RequestParam(required = false) unreadOnly: Boolean = false,
        @RequestParam(required = false) type: NotificationType?
    ): ResponseEntity<ApiResponse<List<NotificationVO>>> {
        val notifications = when {
            unreadOnly -> notificationService.getUnreadNotifications(userId)
            type != null -> notificationService.getNotificationsByRecipientAndType(userId, type)
            else -> notificationService.getNotificationsByRecipient(userId)
        }

        val notificationVos = notifications.map { notificationVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = notificationVos))
    }

    @GetMapping("/user/{userId}/summary")
    fun getUserNotificationSummary(@PathVariable userId: UUID): ResponseEntity<ApiResponse<NotificationSummaryVO>> {
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
        
        return ResponseEntity.ok(ApiResponse(data = summary))
    }

    @PutMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<ApiResponse<NotificationVO>> {
        val notification = notificationService.markAsRead(id, userId)
        return ResponseEntity.ok(ApiResponse(data = notificationVOMapper.toVO(notification)))
    }

    @PutMapping("/user/{userId}/read-all")
    fun markAllAsRead(@PathVariable userId: UUID): ResponseEntity<ApiResponse<MessageVO>> {
        notificationService.markAllAsRead(userId)
        return ResponseEntity.ok(ApiResponse(data = MessageVO("All notifications marked as read")))
    }

    @PostMapping("/mark-read")
    fun markMultipleAsRead(@RequestBody markReadDto: MarkReadDto): ResponseEntity<ApiResponse<MessageVO>> {
        notificationService.markMultipleAsRead(markReadDto.notificationIds, markReadDto.userId)
        return ResponseEntity.ok(ApiResponse(data = MessageVO("Selected notifications marked as read")))
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
    fun getNotificationTypes(): ResponseEntity<ApiResponse<List<String>>> {
        return ResponseEntity.ok(ApiResponse(data = NotificationType.values().map { it.name }))
    }

    @GetMapping("/type/{type}")
    fun getNotificationsByType(@PathVariable type: NotificationType): ResponseEntity<ApiResponse<List<NotificationVO>>> {
        val notifications = notificationService.getNotificationsByType(type)
        val notificationVos = notifications.map { notificationVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = notificationVos))
    }

    @DeleteMapping("/cleanup")
    fun cleanupOldNotifications(@RequestParam(defaultValue = "30") daysOld: Int): ResponseEntity<ApiResponse<MessageVO>> {
        notificationService.deleteOldNotifications(daysOld)
        return ResponseEntity.ok(ApiResponse(data = MessageVO("Old notifications cleaned up successfully")))
    }

    @GetMapping("/stats")
    fun getNotificationStats(): ResponseEntity<ApiResponse<NotificationStatsVO>> {
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
        
        return ResponseEntity.ok(ApiResponse(data = stats))
    }

    // 便利接口：发送特定类型通知
    @PostMapping("/assignment/{assignmentId}/created")
    fun notifyAssignmentCreated(
        @PathVariable assignmentId: UUID,
        @RequestParam courseId: UUID,
        @RequestParam teacherId: UUID
    ): ResponseEntity<ApiResponse<MessageVO>> {
        notificationService.notifyAssignmentCreated(assignmentId, courseId, teacherId)
        return ResponseEntity.ok(ApiResponse(data = MessageVO("Assignment creation notifications sent")))
    }

    @PostMapping("/grade/{submissionId}/published")
    fun notifyGradePublished(
        @PathVariable submissionId: UUID,
        @RequestParam score: BigDecimal,
        @RequestParam studentId: UUID,
        @RequestParam teacherId: UUID
    ): ResponseEntity<ApiResponse<NotificationVO>> {
        val notification = notificationService.notifyGradePublished(
            submissionId = submissionId,
            score = score,
            studentId = studentId,
            teacherId = teacherId
        )
        return ResponseEntity.ok(ApiResponse(data = notificationVOMapper.toVO(notification)))
    }

    // 异常处理
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Map<String, String>>> {
        return ResponseEntity.badRequest().body(
            ApiResponse(data = mapOf(
                "error" to "Bad Request",
                "message" to (ex.message ?: "Invalid request")
            ))
        )
    }
}

package edu.jimei.janus.domain.notification

import edu.jimei.janus.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class NotificationType {
    ASSIGNMENT,
    GRADE,
    SYSTEM,
    COURSE
}

@Entity
@Table(name = "janus_notifications")
@EntityListeners(AuditingEntityListener::class)
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false, length = 255)
    var title: String,


    @Column(nullable = false)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: NotificationType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    var sender: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    var recipient: User,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
) {
    fun markAsRead() {
        isRead = true
        readAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

package edu.jimei.janus.domain.user

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "janus_users")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
    var username: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Column(nullable = false, unique = true)
    var email: String,

    var displayName: String?,

    var avatarUrl: String?,

    @Column(nullable = false, length = 20)
    var role: String,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: OffsetDateTime? = null
) 
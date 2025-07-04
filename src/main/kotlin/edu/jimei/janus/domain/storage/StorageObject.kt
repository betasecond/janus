package edu.jimei.janus.domain.storage

import edu.jimei.janus.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class EmbeddingStatus {
    PENDING,
    COMPLETED,
    FAILED
}

@Entity
@Table(name = "janus_storage_objects")
@EntityListeners(AuditingEntityListener::class)
class StorageObject(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 1024)
    var objectKey: String,

    @Column(nullable = false)
    var originalFilename: String,

    @Column(nullable = false)
    var fileSize: Long,

    var contentType: String?,

    @Column(nullable = false)
    var storageProvider: String,

    @Column(nullable = false)
    var bucketName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var embeddingStatus: EmbeddingStatus = EmbeddingStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    var uploader: User? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null

) 
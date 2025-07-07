package edu.jimei.janus.controller.vo

import edu.jimei.janus.controller.vo.common.UserVO
import edu.jimei.janus.controller.vo.common.toVo
import edu.jimei.janus.domain.storage.EmbeddingStatus
import edu.jimei.janus.domain.storage.StorageObject
import java.time.LocalDateTime
import java.util.*

data class StorageObjectVO(
    val id: UUID,
    val objectKey: String,
    val originalFilename: String,
    val fileSize: Long,
    val contentType: String?,
    val storageProvider: String,
    val bucketName: String,
    val embeddingStatus: EmbeddingStatus,
    val uploader: UserVO?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

fun StorageObject.toVo(): StorageObjectVO {
    return StorageObjectVO(
        id = this.id ?: throw IllegalArgumentException("StorageObject id cannot be null"),
        objectKey = this.objectKey,
        originalFilename = this.originalFilename,
        fileSize = this.fileSize,
        contentType = this.contentType,
        storageProvider = this.storageProvider,
        bucketName = this.bucketName,
        embeddingStatus = this.embeddingStatus,
        uploader = this.uploader?.toVo(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
} 
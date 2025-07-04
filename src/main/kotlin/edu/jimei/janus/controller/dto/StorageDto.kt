package edu.jimei.janus.controller.dto

import edu.jimei.janus.domain.storage.EmbeddingStatus
import edu.jimei.janus.domain.storage.StorageObject
import java.time.LocalDateTime
import java.util.*

data class StorageObjectDto(
    val id: UUID,
    val objectKey: String,
    val originalFilename: String,
    val fileSize: Long,
    val contentType: String?,
    val storageProvider: String,
    val bucketName: String,
    val embeddingStatus: EmbeddingStatus,
    val uploaderId: UUID?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

fun StorageObject.toDto(): StorageObjectDto {
    return StorageObjectDto(
        id = this.id!!, // Assuming id is never null when converting
        objectKey = this.objectKey,
        originalFilename = this.originalFilename,
        fileSize = this.fileSize,
        contentType = this.contentType,
        storageProvider = this.storageProvider,
        bucketName = this.bucketName,
        embeddingStatus = this.embeddingStatus,
        uploaderId = this.uploader?.id,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
} 
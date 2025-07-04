package edu.jimei.janus.application.service

import com.aliyun.oss.OSS
import edu.jimei.janus.domain.storage.StorageObject
import edu.jimei.janus.domain.storage.StorageObjectRepository
import edu.jimei.janus.domain.user.UserRepository
import edu.jimei.janus.infrastructure.oss.OssProperties
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class OssService(
    private val ossClient: OSS,
    private val properties: OssProperties,
    private val storageObjectRepository: StorageObjectRepository,
    private val userRepository: UserRepository
) {

    fun findById(id: UUID): StorageObject? {
        return storageObjectRepository.findById(id).orElse(null)
    }

    /**
     * Uploads a file to OSS and saves its metadata to the database.
     * @param file The file to upload.
     * @param uploaderId The ID of the user uploading the file.
     * @return The saved metadata of the uploaded object.
     */
    @Transactional
    fun upload(file: MultipartFile, uploaderId: UUID): StorageObject {
        val uploader = userRepository.findById(uploaderId).orElseThrow {
            IllegalArgumentException("User with ID $uploaderId not found")
        }

        val objectKey = generateObjectKey(file.originalFilename)

        // Upload to OSS
        ossClient.putObject(properties.bucketName, objectKey, file.inputStream)

        // Save metadata to database
        val storageObject = StorageObject(
            objectKey = objectKey,
            originalFilename = file.originalFilename ?: "unknown",
            fileSize = file.size,
            contentType = file.contentType,
            storageProvider = "ALIYUN_OSS",
            bucketName = properties.bucketName,
            uploader = uploader
        )
        return storageObjectRepository.save(storageObject)
    }

    /**
     * Generates a presigned URL for accessing an object.
     * @param objectKey The key of the object in OSS.
     * @param expirationInMinutes The duration for which the URL is valid.
     * @return A presigned URL.
     */
    fun generatePresignedUrl(objectKey: String, expirationInMinutes: Long = 60): URL {
        val expiration = Date.from(Instant.now().plusSeconds(expirationInMinutes * 60))
        return ossClient.generatePresignedUrl(properties.bucketName, objectKey, expiration)
    }

    /**
     * Gets an object from OSS as a Spring Resource.
     * @param objectKey The key of the object in OSS.
     * @return A Spring Resource.
     */
    fun getAsResource(objectKey: String): Resource {
        val ossObject = ossClient.getObject(properties.bucketName, objectKey)
        return InputStreamResource(ossObject.objectContent)
    }

    /**
     * Deletes an object from OSS and its metadata from the database.
     * @param objectKey The key of the object to delete.
     */
    @Transactional
    fun delete(objectKey: String) {
        // Delete from OSS
        ossClient.deleteObject(properties.bucketName, objectKey)

        // Delete metadata from database
        val storageObject = storageObjectRepository.findByObjectKey(objectKey)
        if (storageObject != null) {
            storageObjectRepository.delete(storageObject)
        }
    }

    /**
     * Generates a unique object key.
     * E.g., "2025/07/26/uuid-original-filename.txt"
     */
    private fun generateObjectKey(originalFilename: String?): String {
        val sanitizedFilename = originalFilename?.replace("[^a-zA-Z0-9._-]".toRegex(), "_") ?: "file"
        val path = java.time.LocalDate.now().toString().replace("-", "/")
        return "$path/${UUID.randomUUID()}-$sanitizedFilename"
    }
} 
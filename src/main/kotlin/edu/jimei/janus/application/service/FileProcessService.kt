package edu.jimei.janus.application.service

import edu.jimei.janus.domain.storage.EmbeddingStatus
import edu.jimei.janus.domain.storage.StorageObjectRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class FileProcessService(
    private val ossService: OssService,
    private val dataIngestionService: DataIngestionService,
    private val storageObjectRepository: StorageObjectRepository
) {
    private val logger = LoggerFactory.getLogger(FileProcessService::class.java)

    @Transactional
    fun processFile(storageObjectId: UUID) {
        val storageObject = ossService.findById(storageObjectId)
            ?: throw IllegalArgumentException("StorageObject with ID $storageObjectId not found")

        if (storageObject.embeddingStatus == EmbeddingStatus.COMPLETED) {
            logger.warn("File {} has already been processed.", storageObject.objectKey)
            return
        }

        try {
            logger.info("Processing file: ${storageObject.objectKey}")
            val resource = ossService.getAsResource(storageObject.objectKey)

            // Pass the storageObject to provide metadata for document reading
            dataIngestionService.ingest(resource, storageObject)

            storageObject.embeddingStatus = EmbeddingStatus.COMPLETED
            storageObjectRepository.save(storageObject)
            logger.info("Successfully processed file: ${storageObject.objectKey}")

        } catch (e: Exception) {
            logger.error("Failed to process file ${storageObject.objectKey}", e)
            storageObject.embeddingStatus = EmbeddingStatus.FAILED
            storageObjectRepository.save(storageObject)
            // Optionally re-throw or handle exception
            throw e
        }
    }
} 
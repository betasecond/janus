package edu.jimei.janus.infrastructure.pulsar

import edu.jimei.janus.application.service.FileProcessService
import org.slf4j.LoggerFactory
import org.springframework.pulsar.annotation.PulsarListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class FileProcessingListener(
    private val fileProcessService: FileProcessService
) {
    private val logger = LoggerFactory.getLogger(FileProcessingListener::class.java)

    @PulsarListener(
        topics = ["\${app.pulsar.topics.file-processing}"],
        subscriptionName = "\${app.pulsar.topics.file-processing}" // Configurable subscription name
    )
    fun handleFileProcessingRequest(storageObjectId: UUID) {
        logger.info("Received file processing request for ID: $storageObjectId")
        try {
            fileProcessService.processFile(storageObjectId)
        } catch (e: Exception) {
            logger.error("Error processing file with ID $storageObjectId", e)
            // Re-throw the exception to let Pulsar handle the message failure (e.g., retry or dead-letter).
            throw e
        }
    }
} 
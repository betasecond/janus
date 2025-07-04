package edu.jimei.janus.infrastructure.pulsar

import org.slf4j.LoggerFactory
import org.springframework.pulsar.core.PulsarTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FileProcessingProducer(
    private val pulsarTemplate: PulsarTemplate<UUID>,
    private val properties: AppPulsarProperties
) {
    private val logger = LoggerFactory.getLogger(FileProcessingProducer::class.java)

    fun sendFileProcessingRequest(storageObjectId: UUID) {
        val topic = properties.topics.fileProcessing
        pulsarTemplate.send(topic, storageObjectId)
        logger.info("Sent file processing request for ID: $storageObjectId to topic: $topic")
    }
} 
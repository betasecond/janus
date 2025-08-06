package edu.jimei.janus.infrastructure.pulsar

import edu.jimei.janus.controller.dto.TrackingDto
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.api.PulsarClientException
import org.apache.pulsar.client.impl.schema.JSONSchema
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TrackingEventProducer(
    private val pulsarClient: PulsarClient,
    private val appPulsarProperties: AppPulsarProperties
) {
    private val logger = LoggerFactory.getLogger(TrackingEventProducer::class.java)
    private val topicName = appPulsarProperties.topics.trackingEvents
    private val schema = JSONSchema.of(TrackingDto::class.java)

    fun send(trackingDto: TrackingDto) {
        try {
            val producer = pulsarClient.newProducer(schema)
                .topic(topicName)
                .create()

            producer.use {
                val msgId = it.send(trackingDto)
                logger.info("Sent tracking event to pulsar, msgId: {}, event: {}", msgId, trackingDto)
            }
        } catch (e: PulsarClientException) {
            logger.error("Failed to send tracking event to pulsar", e)
        }
    }
}


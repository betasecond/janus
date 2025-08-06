package edu.jimei.janus.infrastructure.pulsar

import edu.jimei.janus.controller.dto.TrackingDto
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.api.PulsarClientException
import org.apache.pulsar.client.impl.schema.JSONSchema
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class TrackingEventListener(
    private val pulsarClient: PulsarClient,
    private val appPulsarProperties: AppPulsarProperties,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    private val topicName = appPulsarProperties.topics.trackingEvents
    private val schema = JSONSchema.of(TrackingDto::class.java)

    @PostConstruct
    fun consumeTrackingEvents() {
        try {
            val consumer = pulsarClient.newConsumer(schema)
                .topic(topicName)
                .subscriptionName("janus-tracking-subscription")
                .subscribe()

            Thread {
                while (true) {
                    try {
                        val msg = consumer.receive()
                        val trackingDto = msg.value
                        logger.info { "Received tracking event: $trackingDto" }

                        // Store in Redis Stream
                        val streamKey = "tracking:stream"
                        val record = redisTemplate.opsForStream<String, Any>().add(streamKey, mapOf("event" to trackingDto))
                        logger.info { "Stored tracking event in Redis Stream with ID: ${record?.value}" }

                        consumer.acknowledge(msg)
                    } catch (e: Exception) {
                        logger.error(e) { "Error processing tracking event" }
                    }
                }
            }.start()
        } catch (e: PulsarClientException) {
            logger.error(e) { "Failed to subscribe to tracking events topic" }
        }
    }
}


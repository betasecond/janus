package edu.jimei.janus.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import edu.jimei.janus.controller.dto.TrackingDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MetricsService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {
    private val streamKey = "tracking:stream"

    fun getLatestTrackingEvents(count: Long): List<TrackingDto> {
        return try {
            val records: List<MapRecord<String, String, Any>>? = redisTemplate.opsForStream<String, Any>()
                .reverseRange(streamKey, Range.unbounded(), Limit().count(count.toInt()))

            if (records == null || records.isEmpty()) {
                return emptyList()
            }

            records.mapNotNull { record ->
                // The value is a map, e.g., {event={...tracking dto fields...}}
                (record.value["event"] as? Map<*, *>)?.let {
                    // Convert the Map back to our DTO
                    objectMapper.convertValue(it, TrackingDto::class.java)
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error fetching tracking events from Redis stream" }
            emptyList()
        }
    }
}

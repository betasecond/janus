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
                try {
                    // The value is a map, e.g., {event={...tracking dto fields...}}
                    val eventData = record.value["event"]
                    
                    when (eventData) {
                        // 处理新格式：直接的Map对象
                        is Map<*, *> -> {
                            // 特殊处理properties字段，如果它是一个Array，说明是旧格式
                            val processedEventData = processEventData(eventData)
                            objectMapper.convertValue(processedEventData, TrackingDto::class.java)
                        }
                        // 处理旧格式：Array格式 ["java.util.LinkedHashMap", {...}]
                        is List<*> -> {
                            if (eventData.size >= 2 && eventData[1] is Map<*, *>) {
                                val processedEventData = processEventData(eventData[1] as Map<*, *>)
                                objectMapper.convertValue(processedEventData, TrackingDto::class.java)
                            } else {
                                null
                            }
                        }
                        else -> {
                            logger.warn { "Unknown event data format: ${eventData?.javaClass}" }
                            null
                        }
                    }
                } catch (e: Exception) {
                    logger.warn(e) { "Failed to convert tracking event record: ${record.value}" }
                    null
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error fetching tracking events from Redis stream" }
            emptyList()
        }
    }

    private fun processEventData(eventData: Map<*, *>): Map<String, Any?> {
        val processedData = mutableMapOf<String, Any?>()
        
        eventData.forEach { (key, value) ->
            val processedValue = when {
                // 处理properties字段的特殊情况
                key == "properties" && value is List<*> && value.size >= 2 -> {
                    // 旧格式: ["java.util.LinkedHashMap", {...}]
                    if (value[1] is Map<*, *>) {
                        value[1] as Map<*, *>
                    } else {
                        value
                    }
                }
                // 处理其他可能的Array格式字段
                value is List<*> && value.size >= 2 && value[0] is String && (value[0] as String).contains("java.util") -> {
                    value[1]
                }
                else -> value
            }
            processedData[key.toString()] = processedValue
        }
        
        return processedData
    }

    /**
     * 清理Redis Stream中的所有tracking events
     * 用于清除包含旧格式数据的stream
     */
    fun clearTrackingEventsStream(): Boolean {
        return try {
            val deleted = redisTemplate.delete(streamKey)
            logger.info { "Cleared tracking events stream, deleted: $deleted" }
            deleted
        } catch (e: Exception) {
            logger.error(e) { "Error clearing tracking events stream" }
            false
        }
    }
}

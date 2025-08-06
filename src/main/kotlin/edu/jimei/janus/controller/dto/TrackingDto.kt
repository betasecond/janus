package edu.jimei.janus.controller.dto

import java.time.ZonedDateTime

/**
 * DTO for tracking events
 */
data class TrackingDto(
    val eventName: String,
    val eventTime: ZonedDateTime,
    val page: String,
    val target: String,
    val userId: Long?,
    val properties: Map<String, Any>?
)


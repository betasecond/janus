package edu.jimei.janus.controller

import edu.jimei.janus.controller.dto.TrackingDto
import edu.jimei.janus.infrastructure.pulsar.TrackingEventProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/track")
class TrackingController(
    private val trackingEventProducer: TrackingEventProducer
) {
    @PostMapping
    fun trackEvent(@RequestBody trackingDto: TrackingDto) {
        // Send the tracking event to Pulsar topic
        trackingEventProducer.send(trackingDto)
    }
}

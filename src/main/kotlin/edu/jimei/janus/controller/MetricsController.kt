package edu.jimei.janus.controller

import edu.jimei.janus.application.service.MetricsService
import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.controller.dto.TrackingDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/metrics")
class MetricsController(private val metricsService: MetricsService) {

    @GetMapping("/tracking-events")
    fun getTrackingEvents(
        @RequestParam(defaultValue = "50") count: Long
    ): ApiResponse<List<TrackingDto>> {
        val events = metricsService.getLatestTrackingEvents(count)
        return ApiResponse( success =true,data =   events)
    }
}


package edu.jimei.janus.controller

import edu.jimei.janus.controller.vo.PerformanceStatsVO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/stats")
class StatsController {

    @GetMapping
    fun getPerformanceStats(@RequestParam(required = false) userId: UUID?): ResponseEntity<PerformanceStatsVO> {
        // NOTE: The userId is received but not yet used in this mocked implementation.
        // A real implementation would fetch stats specific to this user.
        val mockStats = PerformanceStatsVO(
            averageAccuracy = 85.0,
            frequentlyMissedConcepts = listOf("数据结构", "算法")
        )
        return ResponseEntity.ok(mockStats)
    }
} 
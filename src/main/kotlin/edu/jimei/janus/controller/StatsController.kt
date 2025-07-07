package edu.jimei.janus.controller

import edu.jimei.janus.controller.vo.PerformanceStatsVO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController {

    @GetMapping
    fun getPerformanceStats(): ResponseEntity<PerformanceStatsVO> {
        // TODO: Replace with actual data retrieval logic
        val stats = PerformanceStatsVO(
            averageAccuracy = 0.95,
            frequentlyMissedConcepts = listOf("数据结构", "算法")
        )
        return ResponseEntity.ok(stats)
    }
} 
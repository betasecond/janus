package edu.jimei.janus.controller.vo

data class PerformanceStatsVO(
    val averageAccuracy: Double,
    val frequentlyMissedConcepts: List<String>
) 
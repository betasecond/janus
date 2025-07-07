package edu.jimei.janus.controller.vo

import java.math.BigDecimal
import java.util.UUID

data class AssignmentStatsVO(
    val id: UUID,
    val title: String,
    val totalStudents: Long,
    val submissionCount: Long,
    val gradedCount: Long,
    val submissionRate: Double,
    val averageScore: BigDecimal?
) 
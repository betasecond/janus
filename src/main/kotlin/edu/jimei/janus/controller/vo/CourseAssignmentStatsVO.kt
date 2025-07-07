package edu.jimei.janus.controller.vo

import java.time.LocalDateTime
import java.util.UUID

data class CourseAssignmentStatsVO(
    val courseId: UUID,
    val totalAssignments: Int,
    val totalSubmissions: Long,
    val assignments: List<AssignmentStatVO>
)

data class AssignmentStatVO(
    val id: UUID,
    val title: String,
    val submissionCount: Long,
    val dueDate: LocalDateTime?
) 
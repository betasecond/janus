package edu.jimei.janus.domain.assignment.projection

import java.util.UUID

data class AssignmentSubmissionCount(
    val assignmentId: UUID,
    val submissionCount: Long
) 
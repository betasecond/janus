package edu.jimei.janus.domain.course.projection

import java.util.UUID

data class CourseStudentCount(
    val courseId: UUID,
    val studentCount: Long
) 
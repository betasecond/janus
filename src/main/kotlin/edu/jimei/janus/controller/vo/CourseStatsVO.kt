package edu.jimei.janus.controller.vo

import edu.jimei.janus.controller.vo.common.UserVO
import java.time.LocalDateTime
import java.util.UUID

data class CourseStatsVO(
    val id: UUID,
    val name: String,
    val studentCount: Long,
    val teacher: UserVO,
    val createdAt: String
) 
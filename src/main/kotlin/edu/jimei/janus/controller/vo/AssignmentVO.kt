package edu.jimei.janus.controller.vo

import edu.jimei.janus.controller.vo.common.CourseVO
import edu.jimei.janus.controller.vo.common.QuestionVO
import edu.jimei.janus.controller.vo.common.UserVO
import edu.jimei.janus.controller.vo.common.toVo
import edu.jimei.janus.domain.assignment.Assignment
import java.time.LocalDateTime
import java.util.UUID

data class AssignmentVO(
    val id: UUID,
    val title: String,
    val description: String?,
    val course: CourseVO,
    val creator: UserVO,
    val questions: List<QuestionVO>,
    val dueDate: LocalDateTime?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

fun Assignment.toVo(): AssignmentVO {
    return AssignmentVO(
        id = this.id ?: throw IllegalArgumentException("Assignment id cannot be null"),
        title = this.title,
        description = this.description,
        course = this.course.toVo(),
        creator = this.creator.toVo(),
        questions = this.questions.map { it.toVo() },
        dueDate = this.dueDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
} 
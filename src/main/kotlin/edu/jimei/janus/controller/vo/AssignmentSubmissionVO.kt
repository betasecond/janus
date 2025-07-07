package edu.jimei.janus.controller.vo

import edu.jimei.janus.controller.vo.common.QuestionVO
import edu.jimei.janus.controller.vo.common.UserVO
import edu.jimei.janus.controller.vo.common.toVo
import edu.jimei.janus.domain.assignment.Assignment
import edu.jimei.janus.domain.assignment.AssignmentSubmission
import edu.jimei.janus.domain.assignment.SubmissionAnswer
import edu.jimei.janus.domain.assignment.SubmissionStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class AssignmentSubmissionVO(
    val id: UUID,
    val assignment: AssignmentBriefVO,
    val student: UserVO,
    val answers: List<SubmissionAnswerVO>,
    val score: BigDecimal?,
    val status: SubmissionStatus,
    val submittedAt: LocalDateTime?
)

fun AssignmentSubmission.toVo(): AssignmentSubmissionVO {
    return AssignmentSubmissionVO(
        id = this.id ?: throw IllegalArgumentException("AssignmentSubmission id cannot be null"),
        assignment = this.assignment.toBriefVo(),
        student = this.student.toVo(),
        answers = this.answers.map { it.toVo() },
        score = this.score,
        status = this.status,
        submittedAt = this.submittedAt
    )
}

data class AssignmentBriefVO(
    val id: UUID,
    val title: String,
    val dueDate: LocalDateTime?
)

fun Assignment.toBriefVo(): AssignmentBriefVO {
    return AssignmentBriefVO(
        id = this.id ?: throw IllegalArgumentException("Assignment id cannot be null"),
        title = this.title,
        dueDate = this.dueDate
    )
}

data class SubmissionAnswerVO(
    val id: UUID,
    val question: QuestionVO,
    val answer: String?,
    val isCorrect: Boolean?
)

fun SubmissionAnswer.toVo(): SubmissionAnswerVO {
    return SubmissionAnswerVO(
        id = this.id ?: throw IllegalArgumentException("SubmissionAnswer id cannot be null"),
        question = this.question.toVo(),
        answer = this.answer,
        isCorrect = this.isCorrect
    )
} 
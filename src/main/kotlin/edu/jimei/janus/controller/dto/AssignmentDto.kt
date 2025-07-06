package edu.jimei.janus.controller.dto

import edu.jimei.janus.domain.assignment.Assignment
import edu.jimei.janus.domain.assignment.AssignmentSubmission
import edu.jimei.janus.domain.assignment.SubmissionAnswer
import edu.jimei.janus.domain.assignment.SubmissionStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreateAssignmentDto(
    val title: String,
    val description: String?,
    val courseId: UUID,
    val creatorId: UUID,
    val dueDate: LocalDateTime?,
    val questionIds: List<UUID>
)

data class UpdateAssignmentDto(
    val title: String?,
    val description: String?,
    val dueDate: LocalDateTime?,
    val questionIds: List<UUID>?
)

data class AssignmentDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val course: CourseDto,
    val creator: UserDto,
    val questions: List<QuestionDto>,
    val dueDate: LocalDateTime?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class SubmitAssignmentDto(
    val assignmentId: UUID,
    val studentId: UUID,
    val answers: Map<UUID, String> // questionId -> answer content
)

data class AssignmentSubmissionDto(
    val id: UUID,
    val assignment: AssignmentBriefDto,
    val student: UserDto,
    val answers: List<SubmissionAnswerDto>,
    val score: BigDecimal?,
    val status: SubmissionStatus,
    val submittedAt: LocalDateTime?
)

data class AssignmentBriefDto(
    val id: UUID,
    val title: String,
    val dueDate: LocalDateTime?
)

data class SubmissionAnswerDto(
    val id: UUID,
    val question: QuestionDto,
    val answer: String?,
    val isCorrect: Boolean?
)

data class GradeSubmissionDto(
    val scores: Map<UUID, Boolean> // questionId -> isCorrect
)

data class AssignmentStatsDto(
    val id: UUID,
    val title: String,
    val totalStudents: Long,
    val submissionCount: Long,
    val gradedCount: Long,
    val submissionRate: Double,
    val averageScore: BigDecimal?
)

// 扩展函数：Domain对象转DTO
fun Assignment.toDto(): AssignmentDto {
    return AssignmentDto(
        id = this.id ?: throw IllegalArgumentException("Assignment id cannot be null"),
        title = this.title,
        description = this.description,
        course = this.course.toDto(),
        creator = this.creator.toDto(),
        questions = this.questions.map { it.toDto() },
        dueDate = this.dueDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Assignment.toBriefDto(): AssignmentBriefDto {
    return AssignmentBriefDto(
        id = this.id ?: throw IllegalArgumentException("Assignment id cannot be null"),
        title = this.title,
        dueDate = this.dueDate
    )
}

fun AssignmentSubmission.toDto(): AssignmentSubmissionDto {
    return AssignmentSubmissionDto(
        id = this.id ?: throw IllegalArgumentException("AssignmentSubmission id cannot be null"),
        assignment = this.assignment.toBriefDto(),
        student = this.student.toDto(),
        answers = this.answers.map { it.toDto() },
        score = this.score,
        status = this.status,
        submittedAt = this.submittedAt
    )
}

fun SubmissionAnswer.toDto(): SubmissionAnswerDto {
    return SubmissionAnswerDto(
        id = this.id ?: throw IllegalArgumentException("SubmissionAnswer id cannot be null"),
        question = this.question.toDto(),
        answer = this.answer,
        isCorrect = this.isCorrect
    )
}

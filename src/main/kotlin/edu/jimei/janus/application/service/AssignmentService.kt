package edu.jimei.janus.application.service

import edu.jimei.janus.domain.assignment.Assignment
import edu.jimei.janus.domain.assignment.AssignmentRepository
import edu.jimei.janus.domain.assignment.AssignmentSubmission
import edu.jimei.janus.domain.assignment.AssignmentSubmissionRepository
import edu.jimei.janus.domain.assignment.SubmissionAnswer
import edu.jimei.janus.domain.assignment.SubmissionAnswerRepository
import edu.jimei.janus.domain.assignment.SubmissionStatus
import edu.jimei.janus.domain.course.CourseRepository
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionRepository
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class AssignmentService(
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: AssignmentSubmissionRepository,
    private val answerRepository: SubmissionAnswerRepository,
    private val courseRepository: CourseRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository
) {

    fun createAssignment(
        title: String,
        description: String?,
        courseId: UUID,
        creatorId: UUID,
        dueDate: LocalDateTime?,
        questionIds: List<UUID>
    ): Assignment {
        val course = courseRepository.findById(courseId).orElseThrow {
            IllegalArgumentException("Course with ID $courseId not found")
        }
        
        val creator = userRepository.findById(creatorId).orElseThrow {
            IllegalArgumentException("Creator with ID $creatorId not found")
        }

        val questions = questionRepository.findAllById(questionIds).toMutableList()
        if (questions.size != questionIds.size) {
            throw IllegalArgumentException("Some questions not found")
        }

        val assignment = Assignment(
            title = title,
            description = description,
            course = course,
            creator = creator,
            dueDate = dueDate
        )
        assignment.questions = questions

        return assignmentRepository.save(assignment)
    }

    fun findById(assignmentId: UUID): Assignment {
        return assignmentRepository.findById(assignmentId).orElseThrow {
            IllegalArgumentException("Assignment with ID $assignmentId not found")
        }
    }

    fun findByCourse(courseId: UUID): List<Assignment> {
        return assignmentRepository.findByCourseId(courseId)
    }

    fun findByCreator(creatorId: UUID): List<Assignment> {
        return assignmentRepository.findByCreatorId(creatorId)
    }

    fun findByStudent(studentId: UUID): List<Assignment> {
        return assignmentRepository.findByStudentId(studentId)
    }

    fun updateAssignment(
        assignmentId: UUID,
        title: String?,
        description: String?,
        dueDate: LocalDateTime?,
        questionIds: List<UUID>?
    ): Assignment {
        val assignment = findById(assignmentId)

        title?.let { assignment.title = it }
        description?.let { assignment.description = it }
        dueDate?.let { assignment.dueDate = it }
        
        questionIds?.let { ids ->
            val questions = questionRepository.findAllById(ids).toMutableList()
            if (questions.size != ids.size) {
                throw IllegalArgumentException("Some questions not found")
            }
            assignment.questions = questions
        }

        return assignmentRepository.save(assignment)
    }

    fun submitAssignment(
        assignmentId: UUID,
        studentId: UUID,
        answers: Map<UUID, String> // questionId -> answer content
    ): AssignmentSubmission {
        val assignment = findById(assignmentId)
        val student = userRepository.findById(studentId).orElseThrow {
            IllegalArgumentException("Student with ID $studentId not found")
        }

        // 检查学生是否已经提交过
        val existingSubmission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
        if (existingSubmission != null) {
            throw IllegalArgumentException("Student has already submitted this assignment")
        }

        // 检查是否过期
        if (assignment.dueDate != null && LocalDateTime.now().isAfter(assignment.dueDate)) {
            throw IllegalArgumentException("Assignment is past due date")
        }

        val submission = AssignmentSubmission(
            assignment = assignment,
            student = student,
            status = SubmissionStatus.SUBMITTED
        )

        val savedSubmission = submissionRepository.save(submission)

        // 创建答案记录
        val submissionAnswers = answers.map { (questionId, answerContent) ->
            val question = questionRepository.findById(questionId).orElseThrow {
                IllegalArgumentException("Question with ID $questionId not found")
            }
            SubmissionAnswer(
                submission = savedSubmission,
                question = question,
                answer = answerContent
            )
        }

        savedSubmission.answers = submissionAnswers.toMutableList()
        answerRepository.saveAll(submissionAnswers)

        return savedSubmission
    }

    fun gradeSubmission(submissionId: UUID, scores: Map<UUID, Boolean>): AssignmentSubmission {
        val submission = submissionRepository.findById(submissionId).orElseThrow {
            IllegalArgumentException("Submission with ID $submissionId not found")
        }

        submission.status = SubmissionStatus.GRADING

        // 更新每个答案的得分
        submission.answers.forEach { answer ->
            scores[answer.question.id]?.let { isCorrect ->
                answer.isCorrect = isCorrect
            }
        }

        // 计算总分
        val totalScore = submission.answers.sumOf { if (it.isCorrect == true) 1.0 else 0.0 }
        submission.score = totalScore
        submission.status = SubmissionStatus.GRADED

        return submissionRepository.save(submission)
    }

    fun getSubmissionsByAssignment(assignmentId: UUID): List<AssignmentSubmission> {
        return submissionRepository.findByAssignmentId(assignmentId)
    }

    fun getSubmissionsByStudent(studentId: UUID): List<AssignmentSubmission> {
        return submissionRepository.findByStudentId(studentId)
    }

    fun getSubmissionCount(assignmentId: UUID): Long {
        return submissionRepository.countByAssignmentId(assignmentId)
    }

    fun getGradedSubmissionCount(assignmentId: UUID): Long {
        return submissionRepository.countByAssignmentIdAndStatus(assignmentId, SubmissionStatus.GRADED)
    }

    fun deleteAssignment(assignmentId: UUID) {
        val assignment = findById(assignmentId)
        assignmentRepository.delete(assignment)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Assignment> {
        return assignmentRepository.findAll()
    }
}

package edu.jimei.janus.controller

import edu.jimei.janus.application.service.AssignmentService
import edu.jimei.janus.application.service.CourseService
import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.controller.dto.CreateAssignmentDto
import edu.jimei.janus.controller.dto.GradeSubmissionDto
import edu.jimei.janus.controller.dto.SubmitAssignmentDto
import edu.jimei.janus.controller.dto.UpdateAssignmentDto
import edu.jimei.janus.controller.mapper.AssignmentVOMapper
import edu.jimei.janus.controller.vo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/assignments")
class AssignmentController(
    private val assignmentService: AssignmentService,
    private val courseService: CourseService,
    private val assignmentVOMapper: AssignmentVOMapper
) {

    @PostMapping
    fun createAssignment(@RequestBody createDto: CreateAssignmentDto): ResponseEntity<ApiResponse<AssignmentVO>> {
        val assignment = assignmentService.createAssignment(
            title = createDto.title,
            description = createDto.description,
            courseId = createDto.courseId,
            creatorId = createDto.creatorId,
            dueDate = createDto.dueDate,
            questionIds = createDto.questionIds
        )
        
        val assignmentVO = assignmentVOMapper.toVO(assignment)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = assignmentVO))
    }

    @GetMapping("/{id}")
    fun getAssignment(@PathVariable id: UUID): ResponseEntity<ApiResponse<AssignmentVO>> {
        val assignment = assignmentService.findById(id)
        val assignmentVO = assignmentVOMapper.toVO(assignment)
        return ResponseEntity.ok(ApiResponse(data = assignmentVO))
    }

    @GetMapping
    fun getAllAssignments(
        @RequestParam(required = false) courseId: UUID?,
        @RequestParam(required = false) creatorId: UUID?,
        @RequestParam(required = false) studentId: UUID?
    ): ResponseEntity<ApiResponse<List<AssignmentVO>>> {
        val assignments = when {
            courseId != null -> assignmentService.findByCourse(courseId)
            creatorId != null -> assignmentService.findByCreator(creatorId)
            studentId != null -> assignmentService.findByStudent(studentId)
            else -> assignmentService.findAll()
        }

        val assignmentVos = assignments.map { assignmentVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = assignmentVos))
    }

    @PutMapping("/{id}")
    fun updateAssignment(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateAssignmentDto
    ): ResponseEntity<ApiResponse<AssignmentVO>> {
        val updatedAssignment = assignmentService.updateAssignment(
            assignmentId = id,
            title = updateDto.title,
            description = updateDto.description,
            dueDate = updateDto.dueDate,
            questionIds = updateDto.questionIds
        )
        
        val assignmentVO = assignmentVOMapper.toVO(updatedAssignment)
        return ResponseEntity.ok(ApiResponse(data = assignmentVO))
    }

    @DeleteMapping("/{id}")
    fun deleteAssignment(@PathVariable id: UUID): ResponseEntity<ApiResponse<Void>> {
        assignmentService.deleteAssignment(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/submit")
    fun submitAssignment(@RequestBody submitDto: SubmitAssignmentDto): ResponseEntity<ApiResponse<AssignmentSubmissionVO>> {
        val submission = assignmentService.submitAssignment(
            assignmentId = submitDto.assignmentId,
            studentId = submitDto.studentId,
            answers = submitDto.answers
        )
        
        val submissionVO = assignmentVOMapper.toVO(submission)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = submissionVO))
    }

    @GetMapping("/{id}/submissions")
    fun getAssignmentSubmissions(@PathVariable id: UUID): ResponseEntity<ApiResponse<List<AssignmentSubmissionVO>>> {
        val submissions = assignmentService.getSubmissionsByAssignment(id)
        val submissionVos = submissions.map { assignmentVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = submissionVos))
    }

    @GetMapping("/submissions/{submissionId}")
    fun getSubmission(@PathVariable submissionId: UUID): ResponseEntity<ApiResponse<AssignmentSubmissionVO>> {
        val submission = assignmentService.findSubmissionById(submissionId)
        val submissionVO = assignmentVOMapper.toVO(submission)
        return ResponseEntity.ok(ApiResponse(data = submissionVO))
    }

    @PostMapping("/submissions/{submissionId}/grade")
    fun gradeSubmission(
        @PathVariable submissionId: UUID,
        @RequestBody gradeDto: GradeSubmissionDto
    ): ResponseEntity<ApiResponse<AssignmentSubmissionVO>> {
        val gradedSubmission = assignmentService.gradeSubmission(submissionId, gradeDto.scores)
        val submissionVO = assignmentVOMapper.toVO(gradedSubmission)
        return ResponseEntity.ok(ApiResponse(data = submissionVO))
    }

    @GetMapping("/student/{studentId}")
    fun getStudentSubmissions(@PathVariable studentId: UUID): ResponseEntity<ApiResponse<List<AssignmentSubmissionVO>>> {
        val submissions = assignmentService.getSubmissionsByStudent(studentId)
        val submissionVos = submissions.map { assignmentVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = submissionVos))
    }

    @GetMapping("/{id}/stats")
    fun getAssignmentStats(@PathVariable id: UUID): ResponseEntity<ApiResponse<AssignmentStatsVO>> {
        val assignment = assignmentService.findById(id)
        val submissionCount = assignmentService.getSubmissionCount(id)
        val gradedCount = assignmentService.getGradedSubmissionCount(id)
        val courseStudentCount = courseService.getStudentCount(assignment.course.id ?: throw IllegalStateException("Assignment's course ID cannot be null"))
        
        val submissionRate = if (courseStudentCount > 0) {
            (submissionCount.toDouble() / courseStudentCount.toDouble()) * 100
        } else 0.0
        val submissions = assignmentService.getSubmissionsByAssignment(id)
        val averageScore = if (submissions.isNotEmpty()) {
            val scores = submissions.mapNotNull { it.score }
            if (scores.isNotEmpty()) {
                scores.reduce { acc, score -> acc.add(score) }.divide(BigDecimal.valueOf(scores.size.toLong()))
            } else null
        } else null
        
        val stats = AssignmentStatsVO(
            id = id,
            title = assignment.title,
            totalStudents = courseStudentCount,
            submissionCount = submissionCount,
            gradedCount = gradedCount,
            submissionRate = submissionRate,
            averageScore = averageScore
        )
        
        return ResponseEntity.ok(ApiResponse(data = stats))
    }

    @GetMapping("/course/{courseId}/stats")
    fun getCourseAssignmentStats(@PathVariable courseId: UUID): ResponseEntity<ApiResponse<CourseAssignmentStatsVO>> {
        val assignments = assignmentService.findByCourse(courseId)
        val assignmentIds = assignments.mapNotNull { it.id }

        val submissionCounts = if (assignmentIds.isNotEmpty()) {
            assignmentService.getSubmissionCounts(assignmentIds)
        } else {
            emptyMap()
        }

        val totalAssignments = assignments.size
        val totalSubmissions = submissionCounts.values.sum()
        
        val stats = CourseAssignmentStatsVO(
            courseId = courseId,
            totalAssignments = totalAssignments,
            totalSubmissions = totalSubmissions,
            assignments = assignments.map { assignment ->
                AssignmentStatVO(
                    id = assignment.id ?: throw IllegalStateException("Assignment ID cannot be null"),
                    title = assignment.title,
                    submissionCount = submissionCounts[assignment.id] ?: 0L,
                    dueDate = assignment.dueDate
                )
            }
        )
        
        return ResponseEntity.ok(ApiResponse(data = stats))
    }

    // 异常处理
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "error" to "Bad Request",
                "message" to (ex.message ?: "Invalid request")
            )
        )
    }
}

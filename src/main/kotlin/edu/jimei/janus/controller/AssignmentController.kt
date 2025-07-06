package edu.jimei.janus.controller

import edu.jimei.janus.application.service.AssignmentService
import edu.jimei.janus.application.service.CourseService
import edu.jimei.janus.controller.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = ["*"])
class AssignmentController(
    private val assignmentService: AssignmentService,
    private val courseService: CourseService
) {

    @PostMapping
    fun createAssignment(@RequestBody createDto: CreateAssignmentDto): ResponseEntity<AssignmentDto> {
        val assignment = assignmentService.createAssignment(
            title = createDto.title,
            description = createDto.description,
            courseId = createDto.courseId,
            creatorId = createDto.creatorId,
            dueDate = createDto.dueDate,
            questionIds = createDto.questionIds
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment.toDto())
    }

    @GetMapping("/{id}")
    fun getAssignment(@PathVariable id: UUID): ResponseEntity<AssignmentDto> {
        val assignment = assignmentService.findById(id)
        return ResponseEntity.ok(assignment.toDto())
    }

    @GetMapping
    fun getAllAssignments(
        @RequestParam(required = false) courseId: UUID?,
        @RequestParam(required = false) creatorId: UUID?,
        @RequestParam(required = false) studentId: UUID?
    ): ResponseEntity<List<AssignmentDto>> {
        val assignments = when {
            courseId != null -> assignmentService.findByCourse(courseId)
            creatorId != null -> assignmentService.findByCreator(creatorId)
            studentId != null -> assignmentService.findByStudent(studentId)
            else -> assignmentService.findAll()
        }

        val assignmentDtos = assignments.map { it.toDto() }
        return ResponseEntity.ok(assignmentDtos)
    }

    @PutMapping("/{id}")
    fun updateAssignment(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateAssignmentDto
    ): ResponseEntity<AssignmentDto> {
        val updatedAssignment = assignmentService.updateAssignment(
            assignmentId = id,
            title = updateDto.title,
            description = updateDto.description,
            dueDate = updateDto.dueDate,
            questionIds = updateDto.questionIds
        )
        
        return ResponseEntity.ok(updatedAssignment.toDto())
    }

    @DeleteMapping("/{id}")
    fun deleteAssignment(@PathVariable id: UUID): ResponseEntity<Void> {
        assignmentService.deleteAssignment(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/submit")
    fun submitAssignment(@RequestBody submitDto: SubmitAssignmentDto): ResponseEntity<AssignmentSubmissionDto> {
        val submission = assignmentService.submitAssignment(
            assignmentId = submitDto.assignmentId,
            studentId = submitDto.studentId,
            answers = submitDto.answers
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(submission.toDto())
    }

    @GetMapping("/{id}/submissions")
    fun getAssignmentSubmissions(@PathVariable id: UUID): ResponseEntity<List<AssignmentSubmissionDto>> {
        val submissions = assignmentService.getSubmissionsByAssignment(id)
        val submissionDtos = submissions.map { it.toDto() }
        return ResponseEntity.ok(submissionDtos)
    }

    @GetMapping("/submissions/{submissionId}")
    fun getSubmission(@PathVariable submissionId: UUID): ResponseEntity<AssignmentSubmissionDto> {
        val submissions = assignmentService.getSubmissionsByAssignment(UUID.randomUUID()) // 需要改进查询方式
        val submission = submissions.find { it.id == submissionId }
            ?: throw IllegalArgumentException("Submission with ID $submissionId not found")
        
        return ResponseEntity.ok(submission.toDto())
    }

    @PostMapping("/submissions/{submissionId}/grade")
    fun gradeSubmission(
        @PathVariable submissionId: UUID,
        @RequestBody gradeDto: GradeSubmissionDto
    ): ResponseEntity<AssignmentSubmissionDto> {
        val gradedSubmission = assignmentService.gradeSubmission(submissionId, gradeDto.scores)
        return ResponseEntity.ok(gradedSubmission.toDto())
    }

    @GetMapping("/student/{studentId}")
    fun getStudentSubmissions(@PathVariable studentId: UUID): ResponseEntity<List<AssignmentSubmissionDto>> {
        val submissions = assignmentService.getSubmissionsByStudent(studentId)
        val submissionDtos = submissions.map { it.toDto() }
        return ResponseEntity.ok(submissionDtos)
    }

    @GetMapping("/{id}/stats")
    fun getAssignmentStats(@PathVariable id: UUID): ResponseEntity<AssignmentStatsDto> {
        val assignment = assignmentService.findById(id)
        val submissionCount = assignmentService.getSubmissionCount(id)
        val gradedCount = assignmentService.getGradedSubmissionCount(id)
        val courseStudentCount = courseService.getStudentCount(assignment.course.id!!)
        
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
        
        val stats = AssignmentStatsDto(
            id = id,
            title = assignment.title,
            totalStudents = courseStudentCount,
            submissionCount = submissionCount,
            gradedCount = gradedCount,
            submissionRate = submissionRate,
            averageScore = averageScore
        )
        
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/course/{courseId}/stats")
    fun getCourseAssignmentStats(@PathVariable courseId: UUID): ResponseEntity<Map<String, Any>> {
        val assignments = assignmentService.findByCourse(courseId)
        val totalAssignments = assignments.size
        val totalSubmissions = assignments.sumOf { assignmentService.getSubmissionCount(it.id!!) }
        
        val stats = mapOf(
            "courseId" to courseId,
            "totalAssignments" to totalAssignments,
            "totalSubmissions" to totalSubmissions,
            "assignments" to assignments.map { assignment ->
                mapOf(
                    "id" to assignment.id,
                    "title" to assignment.title,
                    "submissionCount" to assignmentService.getSubmissionCount(assignment.id!!),
                    "dueDate" to assignment.dueDate
                )
            }
        )
        
        return ResponseEntity.ok(stats)
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

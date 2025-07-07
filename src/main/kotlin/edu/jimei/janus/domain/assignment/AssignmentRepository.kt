package edu.jimei.janus.domain.assignment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface AssignmentRepository : JpaRepository<Assignment, UUID> {
    fun findByCourseId(courseId: UUID): List<Assignment>
    
    fun findByCreatorId(creatorId: UUID): List<Assignment>
    
    fun findByDueDateBefore(date: LocalDateTime): List<Assignment>
    
    fun findByDueDateAfter(date: LocalDateTime): List<Assignment>
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.dueDate > :now")
    fun findActiveByCourseId(@Param("courseId") courseId: UUID, @Param("now") now: LocalDateTime = LocalDateTime.now()): List<Assignment>
    
    @Query("SELECT a FROM Assignment a JOIN a.course.students s WHERE s.id = :studentId")
    fun findByStudentId(@Param("studentId") studentId: UUID): List<Assignment>
}

@Repository
interface AssignmentSubmissionRepository : JpaRepository<AssignmentSubmission, UUID> {
    fun findByAssignmentId(assignmentId: UUID): List<AssignmentSubmission>
    
    fun findByStudentId(studentId: UUID): List<AssignmentSubmission>
    
    fun findByAssignmentIdAndStudentId(assignmentId: UUID, studentId: UUID): AssignmentSubmission?
    
    fun findByStatus(status: SubmissionStatus): List<AssignmentSubmission>
    
    fun countByAssignmentId(assignmentId: UUID): Long
    
    @Query("SELECT s.assignment.id, COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.id IN :assignmentIds GROUP BY s.assignment.id")
    fun countByAssignmentIdIn(@Param("assignmentIds") assignmentIds: List<UUID>): List<Array<Any>>
    
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.status = :status")
    fun countByAssignmentIdAndStatus(@Param("assignmentId") assignmentId: UUID, @Param("status") status: SubmissionStatus): Long
}

@Repository
interface SubmissionAnswerRepository : JpaRepository<SubmissionAnswer, UUID> {
    fun findBySubmissionId(submissionId: UUID): List<SubmissionAnswer>
    
    fun findByQuestionId(questionId: UUID): List<SubmissionAnswer>
    
    fun findBySubmissionIdAndQuestionId(submissionId: UUID, questionId: UUID): SubmissionAnswer?
}

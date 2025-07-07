package edu.jimei.janus.domain.course

import edu.jimei.janus.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CourseRepository : JpaRepository<Course, UUID> {
    fun findByTeacherId(teacherId: UUID): List<Course>
    
    @Query("SELECT c FROM Course c JOIN c.students s WHERE s = :student")
    fun findByStudentsContaining(@Param("student") student: User): List<Course>
    
    fun findByNameContainingIgnoreCase(name: String): List<Course>
    
    @Query("SELECT COUNT(e) FROM Course c JOIN c.students e WHERE c.id = :courseId")
    fun countStudentsByCourseId(@Param("courseId") courseId: UUID): Long

    @Query("SELECT c.id, COUNT(s) FROM Course c JOIN c.students s WHERE c.id IN :courseIds GROUP BY c.id")
    fun countStudentsByCourseIdIn(@Param("courseIds") courseIds: List<UUID>): List<Array<Any>>
}

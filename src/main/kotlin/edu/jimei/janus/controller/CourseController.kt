package edu.jimei.janus.controller

import edu.jimei.janus.application.service.CourseService
import edu.jimei.janus.controller.dto.CreateCourseDto
import edu.jimei.janus.controller.dto.EnrollmentDto
import edu.jimei.janus.controller.dto.UpdateCourseDto
import edu.jimei.janus.controller.vo.CourseStatsVO
import edu.jimei.janus.controller.vo.common.CourseVO
import edu.jimei.janus.controller.vo.common.UserVO
import edu.jimei.janus.controller.vo.common.toVo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/courses")
class CourseController(
    private val courseService: CourseService
) {

    @PostMapping
    fun createCourse(@RequestBody createDto: CreateCourseDto): ResponseEntity<CourseVO> {
        val course = courseService.createCourse(
            name = createDto.name,
            description = createDto.description,
            teacherId = createDto.teacherId,
            coverImageUrl = createDto.coverImageUrl
        )
        
        val studentCount = courseService.getStudentCount(course.id ?: throw IllegalStateException("Created course must have an ID"))
        return ResponseEntity.status(HttpStatus.CREATED).body(course.toVo(studentCount))
    }

    @GetMapping("/{id}")
    fun getCourse(@PathVariable id: UUID): ResponseEntity<CourseVO> {
        val course = courseService.findById(id)
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(course.toVo(studentCount))
    }

    @GetMapping
    fun getAllCourses(
        @RequestParam(required = false) teacherId: UUID?,
        @RequestParam(required = false) studentId: UUID?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<List<CourseVO>> {
        val courses = when {
            teacherId != null -> courseService.findAllByTeacher(teacherId)
            studentId != null -> courseService.findAllByStudent(studentId)
            keyword != null -> courseService.searchCourses(keyword)
            else -> courseService.findAll()
        }

        val courseIds = courses.mapNotNull { it.id }
        val studentCounts = if (courseIds.isNotEmpty()) courseService.getStudentCounts(courseIds) else emptyMap()

        val courseVos = courses.map { course ->
            val studentCount = studentCounts[course.id] ?: 0L
            course.toVo(studentCount)
        }

        return ResponseEntity.ok(courseVos)
    }

    @PutMapping("/{id}")
    fun updateCourse(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateCourseDto
    ): ResponseEntity<CourseVO> {
        val updatedCourse = courseService.updateCourse(
            courseId = id,
            name = updateDto.name,
            description = updateDto.description,
            coverImageUrl = updateDto.coverImageUrl
        )
        
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(updatedCourse.toVo(studentCount))
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(@PathVariable id: UUID): ResponseEntity<Void> {
        courseService.deleteCourse(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/enroll")
    fun enrollStudent(
        @PathVariable id: UUID,
        @RequestBody enrollmentDto: EnrollmentDto
    ): ResponseEntity<CourseVO> {
        val course = courseService.enrollStudent(id, enrollmentDto.studentId)
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(course.toVo(studentCount))
    }

    @DeleteMapping("/{id}/enroll/{studentId}")
    fun unenrollStudent(
        @PathVariable id: UUID,
        @PathVariable studentId: UUID
    ): ResponseEntity<CourseVO> {
        val course = courseService.unenrollStudent(id, studentId)
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(course.toVo(studentCount))
    }

    @GetMapping("/{id}/students")
    fun getCourseStudents(@PathVariable id: UUID): ResponseEntity<List<UserVO>> {
        val course = courseService.findById(id)
        val students = course.students.map { it.toVo() }
        return ResponseEntity.ok(students)    }

    @GetMapping("/{id}/stats")
    fun getCourseStats(@PathVariable id: UUID): ResponseEntity<CourseStatsVO> {
        val studentCount = courseService.getStudentCount(id)
        val course = courseService.findById(id)
        
        val stats = CourseStatsVO(
            id = id,
            name = course.name,
            studentCount = studentCount,
            teacher = course.teacher.toVo(),
            createdAt = course.createdAt?.toString() ?: ""
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

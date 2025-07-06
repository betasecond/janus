package edu.jimei.janus.controller

import edu.jimei.janus.application.service.CourseService
import edu.jimei.janus.controller.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = ["*"]) // 开发环境，生产环境需要配置具体域名
class CourseController(
    private val courseService: CourseService
) {

    @PostMapping
    fun createCourse(@RequestBody createDto: CreateCourseDto): ResponseEntity<CourseDto> {
        val course = courseService.createCourse(
            name = createDto.name,
            description = createDto.description,
            teacherId = createDto.teacherId,
            coverImageUrl = createDto.coverImageUrl
        )
        
        val studentCount = courseService.getStudentCount(course.id!!)
        return ResponseEntity.status(HttpStatus.CREATED).body(course.toDto(studentCount))
    }

    @GetMapping("/{id}")
    fun getCourse(@PathVariable id: UUID): ResponseEntity<CourseDto> {
        val course = courseService.findById(id)
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(course.toDto(studentCount))
    }

    @GetMapping
    fun getAllCourses(
        @RequestParam(required = false) teacherId: UUID?,
        @RequestParam(required = false) studentId: UUID?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<List<CourseDto>> {
        val courses = when {
            teacherId != null -> courseService.findAllByTeacher(teacherId)
            studentId != null -> courseService.findAllByStudent(studentId)
            keyword != null -> courseService.searchCourses(keyword)
            else -> courseService.findAll()
        }

        val courseDtos = courses.map { course ->
            val studentCount = courseService.getStudentCount(course.id!!)
            course.toDto(studentCount)
        }

        return ResponseEntity.ok(courseDtos)
    }

    @PutMapping("/{id}")
    fun updateCourse(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateCourseDto
    ): ResponseEntity<CourseDto> {
        val updatedCourse = courseService.updateCourse(
            courseId = id,
            name = updateDto.name,
            description = updateDto.description,
            coverImageUrl = updateDto.coverImageUrl
        )
        
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(updatedCourse.toDto(studentCount))
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
    ): ResponseEntity<CourseDto> {
        val course = courseService.enrollStudent(id, enrollmentDto.studentId)
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(course.toDto(studentCount))
    }

    @DeleteMapping("/{id}/enroll/{studentId}")
    fun unenrollStudent(
        @PathVariable id: UUID,
        @PathVariable studentId: UUID
    ): ResponseEntity<CourseDto> {
        val course = courseService.unenrollStudent(id, studentId)
        val studentCount = courseService.getStudentCount(id)
        return ResponseEntity.ok(course.toDto(studentCount))
    }

    @GetMapping("/{id}/students")
    fun getCourseStudents(@PathVariable id: UUID): ResponseEntity<List<UserDto>> {
        val course = courseService.findById(id)
        val students = course.students.map { it.toDto() }
        return ResponseEntity.ok(students)    }

    @GetMapping("/{id}/stats")
    fun getCourseStats(@PathVariable id: UUID): ResponseEntity<Map<String, Any>> {
        val studentCount = courseService.getStudentCount(id)
        val course = courseService.findById(id)
        
        val stats: Map<String, Any> = mapOf(
            "id" to id,
            "name" to course.name,
            "studentCount" to studentCount,
            "teacher" to course.teacher.toDto(),
            "createdAt" to (course.createdAt?.toString() ?: "")
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

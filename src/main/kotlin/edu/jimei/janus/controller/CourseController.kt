package edu.jimei.janus.controller

import edu.jimei.janus.application.service.CourseService
import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.controller.dto.CreateCourseDto
import edu.jimei.janus.controller.dto.EnrollmentDto
import edu.jimei.janus.controller.dto.UpdateCourseDto
import edu.jimei.janus.controller.mapper.CourseVOMapper
import edu.jimei.janus.controller.mapper.CourseEnrollmentVOMapper
import edu.jimei.janus.controller.mapper.UserVOMapper
import edu.jimei.janus.controller.vo.CourseVO
import edu.jimei.janus.controller.vo.CourseEnrollmentStatsVO
import edu.jimei.janus.controller.vo.CourseStatsVO
import edu.jimei.janus.controller.vo.UserVO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/courses")
class CourseController(
    private val courseService: CourseService,
    private val courseVOMapper: CourseVOMapper,
    private val courseEnrollmentVOMapper: CourseEnrollmentVOMapper,
    private val userVOMapper: UserVOMapper
) {

    @PostMapping
    fun createCourse(@RequestBody createDto: CreateCourseDto): ResponseEntity<ApiResponse<CourseVO>> {
        val course = courseService.createCourse(
            name = createDto.name,
            description = createDto.description,
            teacherId = createDto.teacherId,
            coverImageUrl = createDto.coverImageUrl
        )
        
        val courseVO = courseVOMapper.toVO(course)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = courseVO))
    }

    @GetMapping("/{id}")
    fun getCourse(@PathVariable id: UUID): ResponseEntity<ApiResponse<CourseVO>> {
        val course = courseService.findById(id)
        val courseVO = courseVOMapper.toVO(course)
        return ResponseEntity.ok(ApiResponse(data = courseVO))
    }

    @GetMapping
    fun getAllCourses(
        @RequestParam(required = false) teacherId: UUID?,
        @RequestParam(required = false) studentId: UUID?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<ApiResponse<List<CourseVO>>> {
        val courses = when {
            teacherId != null -> courseService.findAllByTeacher(teacherId)
            studentId != null -> courseService.findAllByStudent(studentId)
            keyword != null -> courseService.searchCourses(keyword)
            else -> courseService.findAll()
        }

        val courseVOs = courses.map { course ->
            courseVOMapper.toVO(course)
        }

        return ResponseEntity.ok(ApiResponse(data = courseVOs))
    }

    @PutMapping("/{id}")
    fun updateCourse(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateCourseDto
    ): ResponseEntity<ApiResponse<CourseVO>> {
        val updatedCourse = courseService.updateCourse(
            courseId = id,
            name = updateDto.name,
            description = updateDto.description,
            coverImageUrl = updateDto.coverImageUrl
        )
        
        val courseVO = courseVOMapper.toVO(updatedCourse)
        return ResponseEntity.ok(ApiResponse(data = courseVO))
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(@PathVariable id: UUID): ResponseEntity<ApiResponse<String>> {
        courseService.deleteCourse(id)
        return ResponseEntity.ok(ApiResponse(data = "Course deleted successfully"))
    }

    @PostMapping("/{id}/enroll")
    fun enrollStudent(
        @PathVariable id: UUID,
        @RequestBody enrollmentDto: EnrollmentDto
    ): ResponseEntity<ApiResponse<CourseVO>> {
        val course = courseService.enrollStudent(id, enrollmentDto.studentId)
        val courseVO = courseVOMapper.toVO(course)
        return ResponseEntity.ok(ApiResponse(data = courseVO))
    }

    @DeleteMapping("/{id}/enroll/{studentId}")
    fun unenrollStudent(
        @PathVariable id: UUID,
        @PathVariable studentId: UUID
    ): ResponseEntity<ApiResponse<CourseVO>> {
        val course = courseService.unenrollStudent(id, studentId)
        val courseVO = courseVOMapper.toVO(course)
        return ResponseEntity.ok(ApiResponse(data = courseVO))
    }

    @GetMapping("/{id}/students")
    fun getCourseStudents(@PathVariable id: UUID): ResponseEntity<ApiResponse<List<UserVO>>> {
        val course = courseService.findById(id)
        val students = course.students.map { student ->
            userVOMapper.toVO(student)
        }
        return ResponseEntity.ok(ApiResponse(data = students))
    }

    @GetMapping("/{id}/enrollments")
    fun getCourseEnrollments(@PathVariable id: UUID): ResponseEntity<ApiResponse<CourseEnrollmentStatsVO>> {
        val course = courseService.findById(id)
        val enrollmentStats = courseEnrollmentVOMapper.toStatsVO(course)
        return ResponseEntity.ok(ApiResponse(data = enrollmentStats))
    }

    @GetMapping("/{id}/stats")
    fun getCourseStats(@PathVariable id: UUID): ResponseEntity<ApiResponse<CourseStatsVO>> {
        val studentCount = courseService.getStudentCount(id)
        val course = courseService.findById(id)
        
        val stats = CourseStatsVO(
            id = id,
            name = course.name,
            studentCount = studentCount,
            teacher = userVOMapper.toVO(course.teacher),
            createdAt = course.createdAt?.toString() ?: ""
        )
        
        return ResponseEntity.ok(ApiResponse(data = stats))
    }
}

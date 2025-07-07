package edu.jimei.janus.application.service

import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.course.CourseRepository
import edu.jimei.janus.domain.user.User
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class CourseService(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) {

    fun createCourse(name: String, description: String?, teacherId: UUID, coverImageUrl: String? = null): Course {
        val teacher = userRepository.findById(teacherId).orElseThrow {
            IllegalArgumentException("Teacher with ID $teacherId not found")
        }

        val course = Course(
            name = name,
            description = description,
            teacher = teacher,
            coverImageUrl = coverImageUrl
        )

        return courseRepository.save(course)
    }

    fun findById(courseId: UUID): Course {
        return courseRepository.findById(courseId).orElseThrow {
            IllegalArgumentException("Course with ID $courseId not found")
        }
    }

    fun findAllByTeacher(teacherId: UUID): List<Course> {
        return courseRepository.findByTeacherId(teacherId)
    }

    fun findAllByStudent(studentId: UUID): List<Course> {
        val student = userRepository.findById(studentId).orElseThrow {
            IllegalArgumentException("Student with ID $studentId not found")
        }
        return courseRepository.findByStudentsContaining(student)
    }

    fun updateCourse(courseId: UUID, name: String?, description: String?, coverImageUrl: String?): Course {
        val course = findById(courseId)
        
        name?.let { course.name = it }
        description?.let { course.description = it }
        coverImageUrl?.let { course.coverImageUrl = it }

        return courseRepository.save(course)
    }

    fun enrollStudent(courseId: UUID, studentId: UUID): Course {
        val course = findById(courseId)
        val student = userRepository.findById(studentId).orElseThrow {
            IllegalArgumentException("Student with ID $studentId not found")
        }

        if (student.role != "student") {
            throw IllegalArgumentException("User with ID $studentId is not a student")
        }

        course.students.add(student)
        return courseRepository.save(course)
    }

    fun unenrollStudent(courseId: UUID, studentId: UUID): Course {
        val course = findById(courseId)
        val student = userRepository.findById(studentId).orElseThrow {
            IllegalArgumentException("Student with ID $studentId not found")
        }

        course.students.remove(student)
        return courseRepository.save(course)
    }

    fun getStudentCount(courseId: UUID): Long {
        return courseRepository.countStudentsByCourseId(courseId)
    }

    fun getStudentCounts(courseIds: List<UUID>): Map<UUID, Long> {
        if (courseIds.isEmpty()) {
            return emptyMap()
        }
        return courseRepository.countStudentsByCourseIdIn(courseIds)
            .associate { it.courseId to it.studentCount }
    }

    fun searchCourses(keyword: String): List<Course> {
        return courseRepository.findByNameContainingIgnoreCase(keyword)
    }

    fun deleteCourse(courseId: UUID) {
        val course = findById(courseId)
        courseRepository.delete(course)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Course> {
        return courseRepository.findAll()
    }
}

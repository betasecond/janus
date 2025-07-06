package edu.jimei.janus.controller.dto

import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.user.User
import java.time.LocalDateTime
import java.util.UUID

data class CreateCourseDto(
    val name: String,
    val description: String?,
    val teacherId: UUID,
    val coverImageUrl: String?
)

data class UpdateCourseDto(
    val name: String?,
    val description: String?,
    val coverImageUrl: String?
)

data class CourseDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val coverImageUrl: String?,
    val teacher: UserDto,
    val studentCount: Long,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val role: String
)

data class EnrollmentDto(
    val studentId: UUID
)

// 扩展函数：Domain对象转DTO
fun Course.toDto(studentCount: Long = 0): CourseDto {
    return CourseDto(
        id = this.id ?: throw IllegalArgumentException("Course id cannot be null"),
        name = this.name,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        teacher = this.teacher.toDto(),
        studentCount = studentCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id ?: throw IllegalArgumentException("User id cannot be null"),
        username = this.username,
        email = this.email,
        displayName = this.displayName,
        avatarUrl = this.avatarUrl,
        role = this.role
    )
}

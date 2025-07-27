package edu.jimei.janus.controller.mapper

import edu.jimei.janus.controller.vo.UserVO
import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.user.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * CourseVOMapper单元测试
 * 测试课程实体到VO的映射功能，包括teacher对象映射
 */
@ExtendWith(MockitoExtension::class)
class CourseVOMapperTest {

    @Mock
    private lateinit var userVOMapper: UserVOMapper

    @InjectMocks
    private lateinit var courseVOMapper: CourseVOMapper

    @Test
    fun `should map Course to CourseVO correctly`() {
        // Given
        val courseId = UUID.randomUUID()
        val teacherId = UUID.randomUUID()
        
        val teacher = User(
            id = teacherId,
            username = "teacher1",
            passwordHash = "hashedpassword",
            email = "teacher@example.com",
            displayName = "Teacher One",
            avatarUrl = "http://example.com/teacher.jpg",
            role = "teacher",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val course = Course(
            id = courseId,
            name = "Introduction to Computer Science",
            description = "A comprehensive course covering basic computer science concepts",
            teacher = teacher,
            coverImageUrl = "http://example.com/course-cover.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val expectedTeacherVO = UserVO(
            id = teacherId.toString(),
            displayName = "Teacher One",
            email = "teacher@example.com",
            avatarUrl = "http://example.com/teacher.jpg",
            role = "TEACHER"
        )
        
        `when`(userVOMapper.toVO(teacher)).thenReturn(expectedTeacherVO)

        // When
        val courseVO = courseVOMapper.toVO(course)

        // Then
        assertNotNull(courseVO)
        assertEquals(courseId.toString(), courseVO.id)
        assertEquals("Introduction to Computer Science", courseVO.name)
        assertEquals("A comprehensive course covering basic computer science concepts", courseVO.description)
        assertEquals(expectedTeacherVO, courseVO.teacher)
        assertEquals("http://example.com/course-cover.jpg", courseVO.coverImageUrl)
    }

    @Test
    fun `should return empty string when description is null`() {
        // Given
        val courseId = UUID.randomUUID()
        val teacherId = UUID.randomUUID()
        
        val teacher = User(
            id = teacherId,
            username = "teacher1",
            passwordHash = "hashedpassword",
            email = "teacher@example.com",
            displayName = "Teacher One",
            avatarUrl = "http://example.com/teacher.jpg",
            role = "teacher",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val course = Course(
            id = courseId,
            name = "Mathematics",
            description = null, // null description
            teacher = teacher,
            coverImageUrl = "http://example.com/math-cover.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val expectedTeacherVO = UserVO(
            id = teacherId.toString(),
            displayName = "Teacher One",
            email = "teacher@example.com",
            avatarUrl = "http://example.com/teacher.jpg",
            role = "TEACHER"
        )
        
        `when`(userVOMapper.toVO(teacher)).thenReturn(expectedTeacherVO)

        // When
        val courseVO = courseVOMapper.toVO(course)

        // Then
        assertNotNull(courseVO)
        assertEquals(courseId.toString(), courseVO.id)
        assertEquals("Mathematics", courseVO.name)
        assertEquals("", courseVO.description) // Should return empty string when description is null
        assertEquals(expectedTeacherVO, courseVO.teacher)
        assertEquals("http://example.com/math-cover.jpg", courseVO.coverImageUrl)
    }

    @Test
    fun `should handle null coverImageUrl`() {
        // Given
        val courseId = UUID.randomUUID()
        val teacherId = UUID.randomUUID()
        
        val teacher = User(
            id = teacherId,
            username = "teacher1",
            passwordHash = "hashedpassword",
            email = "teacher@example.com",
            displayName = "Teacher One",
            avatarUrl = "http://example.com/teacher.jpg",
            role = "teacher",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val course = Course(
            id = courseId,
            name = "Physics",
            description = "Advanced physics course",
            teacher = teacher,
            coverImageUrl = null, // null cover image
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val expectedTeacherVO = UserVO(
            id = teacherId.toString(),
            displayName = "Teacher One",
            email = "teacher@example.com",
            avatarUrl = "http://example.com/teacher.jpg",
            role = "TEACHER"
        )
        
        `when`(userVOMapper.toVO(teacher)).thenReturn(expectedTeacherVO)

        // When
        val courseVO = courseVOMapper.toVO(course)

        // Then
        assertNotNull(courseVO)
        assertEquals(courseId.toString(), courseVO.id)
        assertEquals("Physics", courseVO.name)
        assertEquals("Advanced physics course", courseVO.description)
        assertEquals(expectedTeacherVO, courseVO.teacher)
        assertEquals(null, courseVO.coverImageUrl) // Should preserve null value
    }
}
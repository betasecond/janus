package edu.jimei.janus.controller.mapper

import edu.jimei.janus.common.EnumConverter
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
 * UserVOMapper单元测试
 * 测试用户实体到VO的映射功能，包括字段映射和角色转换
 */
@ExtendWith(MockitoExtension::class)
class UserVOMapperTest {

    @Mock
    private lateinit var enumConverter: EnumConverter

    @InjectMocks
    private lateinit var userVOMapper: UserVOMapper

    @Test
    fun `should map User to UserVO correctly with displayName`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            username = "testuser",
            passwordHash = "hashedpassword",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = "http://example.com/avatar.jpg",
            role = "teacher",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(enumConverter.convertUserRole("teacher")).thenReturn("TEACHER")

        // When
        val userVO = userVOMapper.toVO(user)

        // Then
        assertNotNull(userVO)
        assertEquals(userId.toString(), userVO.id)
        assertEquals("Test User", userVO.displayName)
        assertEquals("test@example.com", userVO.email)
        assertEquals("http://example.com/avatar.jpg", userVO.avatarUrl)
        assertEquals("TEACHER", userVO.role)
    }

    @Test
    fun `should use username when displayName is null`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            username = "testuser",
            passwordHash = "hashedpassword",
            email = "test@example.com",
            displayName = null,
            avatarUrl = "http://example.com/avatar.jpg",
            role = "student",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(enumConverter.convertUserRole("student")).thenReturn("STUDENT")

        // When
        val userVO = userVOMapper.toVO(user)

        // Then
        assertNotNull(userVO)
        assertEquals(userId.toString(), userVO.id)
        assertEquals("testuser", userVO.displayName) // Should use username when displayName is null
        assertEquals("test@example.com", userVO.email)
        assertEquals("http://example.com/avatar.jpg", userVO.avatarUrl)
        assertEquals("STUDENT", userVO.role)
    }

    @Test
    fun `should return empty string when avatarUrl is null`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            username = "testuser",
            passwordHash = "hashedpassword",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = null,
            role = "admin",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(enumConverter.convertUserRole("admin")).thenReturn("ADMIN")

        // When
        val userVO = userVOMapper.toVO(user)

        // Then
        assertNotNull(userVO)
        assertEquals(userId.toString(), userVO.id)
        assertEquals("Test User", userVO.displayName)
        assertEquals("test@example.com", userVO.email)
        assertEquals("", userVO.avatarUrl) // Should return empty string when avatarUrl is null
        assertEquals("ADMIN", userVO.role)
    }

    @Test
    fun `should convert all role types correctly`() {
        // Given
        val userId = UUID.randomUUID()
        val baseUser = User(
            id = userId,
            username = "testuser",
            passwordHash = "hashedpassword",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = "http://example.com/avatar.jpg",
            role = "teacher",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Test teacher role
        `when`(enumConverter.convertUserRole("teacher")).thenReturn("TEACHER")
        var userVO = userVOMapper.toVO(baseUser)
        assertEquals("TEACHER", userVO.role)

        // Test student role
        baseUser.role = "student"
        `when`(enumConverter.convertUserRole("student")).thenReturn("STUDENT")
        userVO = userVOMapper.toVO(baseUser)
        assertEquals("STUDENT", userVO.role)

        // Test admin role
        baseUser.role = "admin"
        `when`(enumConverter.convertUserRole("admin")).thenReturn("ADMIN")
        userVO = userVOMapper.toVO(baseUser)
        assertEquals("ADMIN", userVO.role)
    }

    @Test
    fun `should handle edge case with empty displayName`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            username = "testuser",
            passwordHash = "hashedpassword",
            email = "test@example.com",
            displayName = "",
            avatarUrl = "http://example.com/avatar.jpg",
            role = "student",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(enumConverter.convertUserRole("student")).thenReturn("STUDENT")

        // When
        val userVO = userVOMapper.toVO(user)

        // Then
        assertNotNull(userVO)
        assertEquals("", userVO.displayName) // Should use empty displayName as provided (not null)
        assertEquals("STUDENT", userVO.role)
    }
}
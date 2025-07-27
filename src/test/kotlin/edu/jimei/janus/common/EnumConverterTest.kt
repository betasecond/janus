package edu.jimei.janus.common

import edu.jimei.janus.domain.assignment.SubmissionStatus
import edu.jimei.janus.domain.notification.NotificationType
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.QuestionType
import edu.jimei.janus.domain.storage.EmbeddingStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * EnumConverter单元测试
 * 测试枚举转换器的所有转换功能，包括正向转换、反向转换、边界情况和异常处理
 */
class EnumConverterTest {

    private val enumConverter = EnumConverter()

    // ========== 用户角色转换测试 ==========

    @Test
    fun `should convert user roles to uppercase correctly`() {
        assertEquals("TEACHER", enumConverter.convertUserRole("teacher"))
        assertEquals("STUDENT", enumConverter.convertUserRole("student"))
        assertEquals("ADMIN", enumConverter.convertUserRole("admin"))
    }

    @Test
    fun `should handle mixed case user roles`() {
        assertEquals("TEACHER", enumConverter.convertUserRole("Teacher"))
        assertEquals("STUDENT", enumConverter.convertUserRole("STUDENT"))
        assertEquals("ADMIN", enumConverter.convertUserRole("AdMiN"))
    }

    @Test
    fun `should convert user roles from frontend correctly`() {
        assertEquals("teacher", enumConverter.convertUserRoleFromFrontend("TEACHER"))
        assertEquals("student", enumConverter.convertUserRoleFromFrontend("STUDENT"))
        assertEquals("admin", enumConverter.convertUserRoleFromFrontend("ADMIN"))
    }

    @Test
    fun `should handle mixed case frontend user roles`() {
        assertEquals("teacher", enumConverter.convertUserRoleFromFrontend("teacher"))
        assertEquals("student", enumConverter.convertUserRoleFromFrontend("Student"))
        assertEquals("admin", enumConverter.convertUserRoleFromFrontend("admin"))
    }

    // ========== 题目类型转换测试 ==========

    @Test
    fun `should convert question types correctly`() {
        assertEquals("SINGLE_CHOICE", enumConverter.convertQuestionType(QuestionType.MULTIPLE_CHOICE))
        assertEquals("TRUE_FALSE", enumConverter.convertQuestionType(QuestionType.TRUE_FALSE))
        assertEquals("SHORT_ANSWER", enumConverter.convertQuestionType(QuestionType.SHORT_ANSWER))
        assertEquals("ESSAY", enumConverter.convertQuestionType(QuestionType.ESSAY))
    }

    @Test
    fun `should convert question types from frontend correctly`() {
        assertEquals(QuestionType.MULTIPLE_CHOICE, enumConverter.convertQuestionTypeFromFrontend("SINGLE_CHOICE"))
        assertEquals(QuestionType.TRUE_FALSE, enumConverter.convertQuestionTypeFromFrontend("TRUE_FALSE"))
        assertEquals(QuestionType.SHORT_ANSWER, enumConverter.convertQuestionTypeFromFrontend("SHORT_ANSWER"))
        assertEquals(QuestionType.ESSAY, enumConverter.convertQuestionTypeFromFrontend("ESSAY"))
    }

    @Test
    fun `should handle mixed case frontend question types`() {
        assertEquals(QuestionType.MULTIPLE_CHOICE, enumConverter.convertQuestionTypeFromFrontend("single_choice"))
        assertEquals(QuestionType.TRUE_FALSE, enumConverter.convertQuestionTypeFromFrontend("true_false"))
        assertEquals(QuestionType.SHORT_ANSWER, enumConverter.convertQuestionTypeFromFrontend("short_answer"))
        assertEquals(QuestionType.ESSAY, enumConverter.convertQuestionTypeFromFrontend("essay"))
    }

    @Test
    fun `should throw exception for unknown question type from frontend`() {
        assertThrows<IllegalArgumentException> {
            enumConverter.convertQuestionTypeFromFrontend("UNKNOWN_TYPE")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertQuestionTypeFromFrontend("INVALID")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertQuestionTypeFromFrontend("")
        }
    }

    // ========== 难度等级转换测试 ==========

    @Test
    fun `should convert difficulty levels correctly`() {
        assertEquals("EASY", enumConverter.convertDifficulty(Difficulty.EASY))
        assertEquals("MEDIUM", enumConverter.convertDifficulty(Difficulty.MEDIUM))
        assertEquals("HARD", enumConverter.convertDifficulty(Difficulty.HARD))
    }

    @Test
    fun `should convert difficulty levels from frontend correctly`() {
        assertEquals(Difficulty.EASY, enumConverter.convertDifficultyFromFrontend("EASY"))
        assertEquals(Difficulty.MEDIUM, enumConverter.convertDifficultyFromFrontend("MEDIUM"))
        assertEquals(Difficulty.HARD, enumConverter.convertDifficultyFromFrontend("HARD"))
    }

    @Test
    fun `should handle mixed case frontend difficulty levels`() {
        assertEquals(Difficulty.EASY, enumConverter.convertDifficultyFromFrontend("easy"))
        assertEquals(Difficulty.MEDIUM, enumConverter.convertDifficultyFromFrontend("Medium"))
        assertEquals(Difficulty.HARD, enumConverter.convertDifficultyFromFrontend("hard"))
    }

    @Test
    fun `should throw exception for unknown difficulty from frontend`() {
        assertThrows<IllegalArgumentException> {
            enumConverter.convertDifficultyFromFrontend("UNKNOWN")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertDifficultyFromFrontend("SIMPLE")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertDifficultyFromFrontend("")
        }
    }

    // ========== 提交状态转换测试 ==========

    @Test
    fun `should convert submission status correctly`() {
        assertEquals("SUBMITTED", enumConverter.convertSubmissionStatus(SubmissionStatus.SUBMITTED))
        assertEquals("GRADING", enumConverter.convertSubmissionStatus(SubmissionStatus.GRADING))
        assertEquals("GRADED", enumConverter.convertSubmissionStatus(SubmissionStatus.GRADED))
    }

    @Test
    fun `should convert submission status from frontend correctly`() {
        assertEquals(SubmissionStatus.SUBMITTED, enumConverter.convertSubmissionStatusFromFrontend("SUBMITTED"))
        assertEquals(SubmissionStatus.GRADING, enumConverter.convertSubmissionStatusFromFrontend("GRADING"))
        assertEquals(SubmissionStatus.GRADED, enumConverter.convertSubmissionStatusFromFrontend("GRADED"))
    }

    @Test
    fun `should handle mixed case frontend submission status`() {
        assertEquals(SubmissionStatus.SUBMITTED, enumConverter.convertSubmissionStatusFromFrontend("submitted"))
        assertEquals(SubmissionStatus.GRADING, enumConverter.convertSubmissionStatusFromFrontend("Grading"))
        assertEquals(SubmissionStatus.GRADED, enumConverter.convertSubmissionStatusFromFrontend("graded"))
    }

    @Test
    fun `should throw exception for unknown submission status from frontend`() {
        assertThrows<IllegalArgumentException> {
            enumConverter.convertSubmissionStatusFromFrontend("UNKNOWN")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertSubmissionStatusFromFrontend("PENDING")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertSubmissionStatusFromFrontend("")
        }
    }

    // ========== 通知类型转换测试 ==========

    @Test
    fun `should convert notification types correctly`() {
        assertEquals("INFO", enumConverter.convertNotificationType(NotificationType.ASSIGNMENT))
        assertEquals("SUCCESS", enumConverter.convertNotificationType(NotificationType.GRADE))
        assertEquals("WARNING", enumConverter.convertNotificationType(NotificationType.SYSTEM))
        assertEquals("INFO", enumConverter.convertNotificationType(NotificationType.COURSE))
    }

    @Test
    fun `should convert notification types from frontend correctly`() {
        assertEquals(NotificationType.ASSIGNMENT, enumConverter.convertNotificationTypeFromFrontend("INFO"))
        assertEquals(NotificationType.GRADE, enumConverter.convertNotificationTypeFromFrontend("SUCCESS"))
        assertEquals(NotificationType.SYSTEM, enumConverter.convertNotificationTypeFromFrontend("WARNING"))
        assertEquals(NotificationType.SYSTEM, enumConverter.convertNotificationTypeFromFrontend("ERROR"))
    }

    @Test
    fun `should handle mixed case frontend notification types`() {
        assertEquals(NotificationType.ASSIGNMENT, enumConverter.convertNotificationTypeFromFrontend("info"))
        assertEquals(NotificationType.GRADE, enumConverter.convertNotificationTypeFromFrontend("Success"))
        assertEquals(NotificationType.SYSTEM, enumConverter.convertNotificationTypeFromFrontend("warning"))
        assertEquals(NotificationType.SYSTEM, enumConverter.convertNotificationTypeFromFrontend("error"))
    }

    @Test
    fun `should throw exception for unknown notification type from frontend`() {
        assertThrows<IllegalArgumentException> {
            enumConverter.convertNotificationTypeFromFrontend("UNKNOWN")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertNotificationTypeFromFrontend("DEBUG")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertNotificationTypeFromFrontend("")
        }
    }

    // ========== 嵌入状态转换测试 ==========

    @Test
    fun `should convert embedding status correctly`() {
        assertEquals("PENDING", enumConverter.convertEmbeddingStatus(EmbeddingStatus.PENDING))
        assertEquals("COMPLETED", enumConverter.convertEmbeddingStatus(EmbeddingStatus.COMPLETED))
        assertEquals("FAILED", enumConverter.convertEmbeddingStatus(EmbeddingStatus.FAILED))
    }

    @Test
    fun `should convert embedding status from frontend correctly`() {
        assertEquals(EmbeddingStatus.PENDING, enumConverter.convertEmbeddingStatusFromFrontend("PENDING"))
        assertEquals(EmbeddingStatus.COMPLETED, enumConverter.convertEmbeddingStatusFromFrontend("COMPLETED"))
        assertEquals(EmbeddingStatus.FAILED, enumConverter.convertEmbeddingStatusFromFrontend("FAILED"))
    }

    @Test
    fun `should handle mixed case frontend embedding status`() {
        assertEquals(EmbeddingStatus.PENDING, enumConverter.convertEmbeddingStatusFromFrontend("pending"))
        assertEquals(EmbeddingStatus.COMPLETED, enumConverter.convertEmbeddingStatusFromFrontend("Completed"))
        assertEquals(EmbeddingStatus.FAILED, enumConverter.convertEmbeddingStatusFromFrontend("failed"))
    }

    @Test
    fun `should throw exception for unknown embedding status from frontend`() {
        assertThrows<IllegalArgumentException> {
            enumConverter.convertEmbeddingStatusFromFrontend("UNKNOWN")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertEmbeddingStatusFromFrontend("PROCESSING")
        }
        
        assertThrows<IllegalArgumentException> {
            enumConverter.convertEmbeddingStatusFromFrontend("")
        }
    }

    // ========== 边界情况和异常处理测试 ==========

    @Test
    fun `should handle empty strings in user role conversion`() {
        assertEquals("", enumConverter.convertUserRole(""))
        assertEquals("", enumConverter.convertUserRoleFromFrontend(""))
    }

    @Test
    fun `should handle whitespace in user role conversion`() {
        assertEquals("TEACHER", enumConverter.convertUserRole("teacher"))
        assertEquals("STUDENT", enumConverter.convertUserRole("student"))
        // Note: The current implementation doesn't trim whitespace, 
        // so " teacher " would become " TEACHER "
        assertEquals(" TEACHER ", enumConverter.convertUserRole(" teacher "))
        assertEquals(" teacher ", enumConverter.convertUserRoleFromFrontend(" TEACHER "))
    }

    @Test
    fun `should handle null-like strings gracefully`() {
        // These are edge cases that might occur in real scenarios
        assertEquals("NULL", enumConverter.convertUserRole("null"))
        assertEquals("UNDEFINED", enumConverter.convertUserRole("undefined"))
        assertEquals("null", enumConverter.convertUserRoleFromFrontend("NULL"))
        assertEquals("undefined", enumConverter.convertUserRoleFromFrontend("UNDEFINED"))
    }

    @Test
    fun `should be case insensitive for all conversions`() {
        // User roles
        assertEquals("TEACHER", enumConverter.convertUserRole("TEACHER"))
        assertEquals("TEACHER", enumConverter.convertUserRole("Teacher"))
        assertEquals("TEACHER", enumConverter.convertUserRole("tEaChEr"))
        
        // Question types from frontend
        assertEquals(QuestionType.MULTIPLE_CHOICE, enumConverter.convertQuestionTypeFromFrontend("Single_Choice"))
        assertEquals(QuestionType.TRUE_FALSE, enumConverter.convertQuestionTypeFromFrontend("True_False"))
        
        // Difficulty levels from frontend
        assertEquals(Difficulty.EASY, enumConverter.convertDifficultyFromFrontend("Easy"))
        assertEquals(Difficulty.MEDIUM, enumConverter.convertDifficultyFromFrontend("Medium"))
        assertEquals(Difficulty.HARD, enumConverter.convertDifficultyFromFrontend("Hard"))
    }

    @Test
    fun `should maintain consistency in round-trip conversions`() {
        // User roles round-trip
        val originalRole = "teacher"
        val convertedRole = enumConverter.convertUserRole(originalRole)
        val backToOriginal = enumConverter.convertUserRoleFromFrontend(convertedRole)
        assertEquals(originalRole, backToOriginal)
        
        // Question types round-trip
        val originalQuestionType = QuestionType.MULTIPLE_CHOICE
        val convertedQuestionType = enumConverter.convertQuestionType(originalQuestionType)
        val backToOriginalQuestionType = enumConverter.convertQuestionTypeFromFrontend(convertedQuestionType)
        assertEquals(originalQuestionType, backToOriginalQuestionType)
        
        // Difficulty round-trip
        val originalDifficulty = Difficulty.MEDIUM
        val convertedDifficulty = enumConverter.convertDifficulty(originalDifficulty)
        val backToOriginalDifficulty = enumConverter.convertDifficultyFromFrontend(convertedDifficulty)
        assertEquals(originalDifficulty, backToOriginalDifficulty)
        
        // Submission status round-trip
        val originalStatus = SubmissionStatus.GRADING
        val convertedStatus = enumConverter.convertSubmissionStatus(originalStatus)
        val backToOriginalStatus = enumConverter.convertSubmissionStatusFromFrontend(convertedStatus)
        assertEquals(originalStatus, backToOriginalStatus)
        
        // Embedding status round-trip
        val originalEmbeddingStatus = EmbeddingStatus.COMPLETED
        val convertedEmbeddingStatus = enumConverter.convertEmbeddingStatus(originalEmbeddingStatus)
        val backToOriginalEmbeddingStatus = enumConverter.convertEmbeddingStatusFromFrontend(convertedEmbeddingStatus)
        assertEquals(originalEmbeddingStatus, backToOriginalEmbeddingStatus)
    }

    @Test
    fun `should handle all enum values without exceptions`() {
        // Test all QuestionType values
        QuestionType.values().forEach { type ->
            val converted = enumConverter.convertQuestionType(type)
            assertNotNull(converted)
            // Should be able to convert back
            val backConverted = enumConverter.convertQuestionTypeFromFrontend(converted)
            assertEquals(type, backConverted)
        }
        
        // Test all Difficulty values
        Difficulty.values().forEach { difficulty ->
            val converted = enumConverter.convertDifficulty(difficulty)
            assertNotNull(converted)
            // Should be able to convert back
            val backConverted = enumConverter.convertDifficultyFromFrontend(converted)
            assertEquals(difficulty, backConverted)
        }
        
        // Test all SubmissionStatus values
        SubmissionStatus.values().forEach { status ->
            val converted = enumConverter.convertSubmissionStatus(status)
            assertNotNull(converted)
            // Should be able to convert back
            val backConverted = enumConverter.convertSubmissionStatusFromFrontend(converted)
            assertEquals(status, backConverted)
        }
        
        // Test all NotificationType values
        NotificationType.values().forEach { type ->
            val converted = enumConverter.convertNotificationType(type)
            assertNotNull(converted)
            // Note: NotificationType has many-to-one mapping, so we can't always convert back exactly
        }
        
        // Test all EmbeddingStatus values
        EmbeddingStatus.values().forEach { status ->
            val converted = enumConverter.convertEmbeddingStatus(status)
            assertNotNull(converted)
            // Should be able to convert back
            val backConverted = enumConverter.convertEmbeddingStatusFromFrontend(converted)
            assertEquals(status, backConverted)
        }
    }


}
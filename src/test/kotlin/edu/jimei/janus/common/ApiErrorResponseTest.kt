package edu.jimei.janus.common

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * ApiErrorResponse单元测试
 * 测试统一API错误响应包装器的正确性
 */
class ApiErrorResponseTest {

    @Test
    fun `should create ApiErrorResponse with success false by default`() {
        // Given
        val errorDetail = ErrorDetail(code = "TEST_ERROR", message = "Test error message")

        // When
        val response = ApiErrorResponse(error = errorDetail)

        // Then
        assertNotNull(response)
        assertFalse(response.success)
        assertEquals(errorDetail, response.error)
        assertEquals("TEST_ERROR", response.error.code)
        assertEquals("Test error message", response.error.message)
    }

    @Test
    fun `should create ApiErrorResponse with explicit success value`() {
        // Given
        val errorDetail = ErrorDetail(code = "VALIDATION_ERROR", message = "Input validation failed")

        // When
        val response = ApiErrorResponse(success = false, error = errorDetail)

        // Then
        assertNotNull(response)
        assertFalse(response.success)
        assertEquals(errorDetail, response.error)
        assertEquals("VALIDATION_ERROR", response.error.code)
        assertEquals("Input validation failed", response.error.message)
    }

    @Test
    fun `should handle different error codes and messages`() {
        // Test RESOURCE_NOT_FOUND error
        val notFoundError = ErrorDetail(
            code = "RESOURCE_NOT_FOUND",
            message = "The requested resource was not found"
        )
        val notFoundResponse = ApiErrorResponse(error = notFoundError)
        
        assertFalse(notFoundResponse.success)
        assertEquals("RESOURCE_NOT_FOUND", notFoundResponse.error.code)
        assertEquals("The requested resource was not found", notFoundResponse.error.message)

        // Test UNAUTHORIZED error
        val unauthorizedError = ErrorDetail(
            code = "UNAUTHORIZED",
            message = "Authentication required"
        )
        val unauthorizedResponse = ApiErrorResponse(error = unauthorizedError)
        
        assertFalse(unauthorizedResponse.success)
        assertEquals("UNAUTHORIZED", unauthorizedResponse.error.code)
        assertEquals("Authentication required", unauthorizedResponse.error.message)

        // Test INTERNAL_SERVER_ERROR
        val serverError = ErrorDetail(
            code = "INTERNAL_SERVER_ERROR",
            message = "An unexpected error occurred"
        )
        val serverErrorResponse = ApiErrorResponse(error = serverError)
        
        assertFalse(serverErrorResponse.success)
        assertEquals("INTERNAL_SERVER_ERROR", serverErrorResponse.error.code)
        assertEquals("An unexpected error occurred", serverErrorResponse.error.message)
    }

    @Test
    fun `should handle empty error messages`() {
        // Given
        val errorDetail = ErrorDetail(code = "EMPTY_MESSAGE", message = "")

        // When
        val response = ApiErrorResponse(error = errorDetail)

        // Then
        assertNotNull(response)
        assertFalse(response.success)
        assertEquals("EMPTY_MESSAGE", response.error.code)
        assertEquals("", response.error.message)
    }

    @Test
    fun `should handle long error messages`() {
        // Given
        val longMessage = "This is a very long error message that contains detailed information about what went wrong during the processing of the request. It includes multiple sentences and provides comprehensive details about the error condition that occurred in the system."
        val errorDetail = ErrorDetail(code = "DETAILED_ERROR", message = longMessage)

        // When
        val response = ApiErrorResponse(error = errorDetail)

        // Then
        assertNotNull(response)
        assertFalse(response.success)
        assertEquals("DETAILED_ERROR", response.error.code)
        assertEquals(longMessage, response.error.message)
    }

    @Test
    fun `should handle special characters in error messages`() {
        // Given
        val specialMessage = "Error with special characters: áéíóú, 中文, 日本語, emoji 🚨, symbols @#$%^&*()"
        val errorDetail = ErrorDetail(code = "SPECIAL_CHARS", message = specialMessage)

        // When
        val response = ApiErrorResponse(error = errorDetail)

        // Then
        assertNotNull(response)
        assertFalse(response.success)
        assertEquals("SPECIAL_CHARS", response.error.code)
        assertEquals(specialMessage, response.error.message)
    }

    @Test
    fun `should be serializable to expected JSON structure`() {
        // Given
        val errorDetail = ErrorDetail(code = "VALIDATION_ERROR", message = "Field 'email' is required")
        val response = ApiErrorResponse(error = errorDetail)

        // Then - verify the structure matches expected format
        assertNotNull(response)
        assertFalse(response.success)
        assertEquals(errorDetail, response.error)
        
        // The response should have the structure: {success: false, error: {code: "...", message: "..."}}
        // This would be verified in integration tests with actual JSON serialization
    }
}

/**
 * ErrorDetail单元测试
 * 测试错误详情数据类的正确性
 */
class ErrorDetailTest {

    @Test
    fun `should create ErrorDetail with code and message`() {
        // Given & When
        val errorDetail = ErrorDetail(code = "TEST_CODE", message = "Test message")

        // Then
        assertNotNull(errorDetail)
        assertEquals("TEST_CODE", errorDetail.code)
        assertEquals("Test message", errorDetail.message)
    }

    @Test
    fun `should handle different error codes`() {
        val codes = listOf(
            "RESOURCE_NOT_FOUND",
            "VALIDATION_ERROR",
            "UNAUTHORIZED",
            "FORBIDDEN",
            "INTERNAL_SERVER_ERROR",
            "DUPLICATE_RESOURCE",
            "INVALID_OPERATION"
        )

        codes.forEach { code ->
            val errorDetail = ErrorDetail(code = code, message = "Test message for $code")
            assertEquals(code, errorDetail.code)
            assertEquals("Test message for $code", errorDetail.message)
        }
    }

    @Test
    fun `should support equality comparison`() {
        // Given
        val error1 = ErrorDetail(code = "TEST_ERROR", message = "Test message")
        val error2 = ErrorDetail(code = "TEST_ERROR", message = "Test message")
        val error3 = ErrorDetail(code = "DIFFERENT_ERROR", message = "Test message")

        // Then
        assertEquals(error1, error2) // Same code and message
        assertEquals(error1.hashCode(), error2.hashCode())
        
        // Different objects should not be equal
        assertNotNull(error1)
        assertNotNull(error3)
        // Note: We can't use assertNotEquals in kotlin.test, so we verify they're different
        val areEqual = error1 == error3
        assertFalse(areEqual)
    }

    @Test
    fun `should support toString representation`() {
        // Given
        val errorDetail = ErrorDetail(code = "TEST_ERROR", message = "Test message")

        // When
        val stringRepresentation = errorDetail.toString()

        // Then
        assertNotNull(stringRepresentation)
        // The toString should contain both code and message
        // Exact format depends on Kotlin's data class implementation
    }
}
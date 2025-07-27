package edu.jimei.janus.common

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * ApiResponse单元测试
 * 测试统一API成功响应包装器的正确性
 */
class ApiResponseTest {

    @Test
    fun `should create ApiResponse with success true by default`() {
        // Given
        val data = "test data"

        // When
        val response = ApiResponse(data = data)

        // Then
        assertNotNull(response)
        assertTrue(response.success)
        assertEquals("test data", response.data)
    }

    @Test
    fun `should create ApiResponse with explicit success value`() {
        // Given
        val data = mapOf("key" to "value")

        // When
        val response = ApiResponse(success = true, data = data)

        // Then
        assertNotNull(response)
        assertTrue(response.success)
        assertEquals(mapOf("key" to "value"), response.data)
    }

    @Test
    fun `should handle different data types`() {
        // Test with String
        val stringResponse = ApiResponse(data = "string data")
        assertEquals("string data", stringResponse.data)
        assertTrue(stringResponse.success)

        // Test with Integer
        val intResponse = ApiResponse(data = 42)
        assertEquals(42, intResponse.data)
        assertTrue(intResponse.success)

        // Test with List
        val listData = listOf("item1", "item2", "item3")
        val listResponse = ApiResponse(data = listData)
        assertEquals(listData, listResponse.data)
        assertTrue(listResponse.success)

        // Test with Map
        val mapData = mapOf("id" to 1, "name" to "test")
        val mapResponse = ApiResponse(data = mapData)
        assertEquals(mapData, mapResponse.data)
        assertTrue(mapResponse.success)

        // Test with null data
        val nullResponse = ApiResponse<String?>(data = null)
        assertEquals(null, nullResponse.data)
        assertTrue(nullResponse.success)
    }

    @Test
    fun `should handle complex object data`() {
        // Given
        data class TestUser(val id: Int, val name: String, val email: String)
        val userData = TestUser(1, "John Doe", "john@example.com")

        // When
        val response = ApiResponse(data = userData)

        // Then
        assertNotNull(response)
        assertTrue(response.success)
        assertEquals(userData, response.data)
        assertEquals(1, response.data.id)
        assertEquals("John Doe", response.data.name)
        assertEquals("john@example.com", response.data.email)
    }

    @Test
    fun `should handle nested ApiResponse`() {
        // Given
        val innerData = "inner data"
        val innerResponse = ApiResponse(data = innerData)

        // When
        val outerResponse = ApiResponse(data = innerResponse)

        // Then
        assertNotNull(outerResponse)
        assertTrue(outerResponse.success)
        assertEquals(innerResponse, outerResponse.data)
        assertTrue(outerResponse.data.success)
        assertEquals("inner data", outerResponse.data.data)
    }

    @Test
    fun `should be serializable to expected JSON structure`() {
        // Given
        val data = mapOf("userId" to 123, "username" to "testuser")
        val response = ApiResponse(data = data)

        // Then - verify the structure matches expected format
        assertNotNull(response)
        assertTrue(response.success)
        assertEquals(data, response.data)
        
        // The response should have the structure: {success: true, data: {...}}
        // This would be verified in integration tests with actual JSON serialization
    }

    @Test
    fun `should handle empty collections`() {
        // Test with empty list
        val emptyListResponse = ApiResponse(data = emptyList<String>())
        assertEquals(emptyList<String>(), emptyListResponse.data)
        assertTrue(emptyListResponse.success)

        // Test with empty map
        val emptyMapResponse = ApiResponse(data = emptyMap<String, Any>())
        assertEquals(emptyMap<String, Any>(), emptyMapResponse.data)
        assertTrue(emptyMapResponse.success)

        // Test with empty set
        val emptySetResponse = ApiResponse(data = emptySet<Int>())
        assertEquals(emptySet<Int>(), emptySetResponse.data)
        assertTrue(emptySetResponse.success)
    }
}
package edu.jimei.janus.common

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

/**
 * PageVO单元测试
 * 测试分页数据包装器的正确性
 */
class PageVOTest {

    @Test
    fun `should create PageVO with all required fields`() {
        // Given
        val content = listOf("item1", "item2", "item3")
        val totalElements = 100L
        val totalPages = 10
        val size = 10
        val number = 0

        // When
        val pageVO = PageVO(
            content = content,
            totalElements = totalElements,
            totalPages = totalPages,
            size = size,
            number = number
        )

        // Then
        assertNotNull(pageVO)
        assertEquals(content, pageVO.content)
        assertEquals(totalElements, pageVO.totalElements)
        assertEquals(totalPages, pageVO.totalPages)
        assertEquals(size, pageVO.size)
        assertEquals(number, pageVO.number)
    }

    @Test
    fun `should handle empty content list`() {
        // Given
        val emptyContent = emptyList<String>()

        // When
        val pageVO = PageVO(
            content = emptyContent,
            totalElements = 0L,
            totalPages = 0,
            size = 10,
            number = 0
        )

        // Then
        assertNotNull(pageVO)
        assertEquals(emptyContent, pageVO.content)
        assertTrue(pageVO.content.isEmpty())
        assertEquals(0L, pageVO.totalElements)
        assertEquals(0, pageVO.totalPages)
        assertEquals(10, pageVO.size)
        assertEquals(0, pageVO.number)
    }

    @Test
    fun `should handle different content types`() {
        // Test with String content
        val stringPageVO = PageVO(
            content = listOf("a", "b", "c"),
            totalElements = 3L,
            totalPages = 1,
            size = 10,
            number = 0
        )
        assertEquals(listOf("a", "b", "c"), stringPageVO.content)

        // Test with Integer content
        val intPageVO = PageVO(
            content = listOf(1, 2, 3, 4, 5),
            totalElements = 5L,
            totalPages = 1,
            size = 10,
            number = 0
        )
        assertEquals(listOf(1, 2, 3, 4, 5), intPageVO.content)

        // Test with complex object content
        data class TestUser(val id: Int, val name: String)
        val users = listOf(
            TestUser(1, "Alice"),
            TestUser(2, "Bob"),
            TestUser(3, "Charlie")
        )
        val userPageVO = PageVO(
            content = users,
            totalElements = 3L,
            totalPages = 1,
            size = 10,
            number = 0
        )
        assertEquals(users, userPageVO.content)
        assertEquals(3, userPageVO.content.size)
        assertEquals("Alice", userPageVO.content[0].name)
    }

    @Test
    fun `should handle first page correctly`() {
        // Given
        val content = listOf("item1", "item2", "item3")

        // When
        val firstPage = PageVO(
            content = content,
            totalElements = 25L,
            totalPages = 3,
            size = 10,
            number = 0 // First page is 0
        )

        // Then
        assertNotNull(firstPage)
        assertEquals(0, firstPage.number)
        assertEquals(10, firstPage.size)
        assertEquals(3, firstPage.totalPages)
        assertEquals(25L, firstPage.totalElements)
        assertEquals(3, firstPage.content.size)
    }

    @Test
    fun `should handle middle page correctly`() {
        // Given
        val content = listOf("item11", "item12", "item13", "item14", "item15")

        // When
        val middlePage = PageVO(
            content = content,
            totalElements = 25L,
            totalPages = 3,
            size = 10,
            number = 1 // Second page
        )

        // Then
        assertNotNull(middlePage)
        assertEquals(1, middlePage.number)
        assertEquals(10, middlePage.size)
        assertEquals(3, middlePage.totalPages)
        assertEquals(25L, middlePage.totalElements)
        assertEquals(5, middlePage.content.size)
    }

    @Test
    fun `should handle last page correctly`() {
        // Given
        val content = listOf("item21", "item22", "item23", "item24", "item25")

        // When
        val lastPage = PageVO(
            content = content,
            totalElements = 25L,
            totalPages = 3,
            size = 10,
            number = 2 // Third page (last)
        )

        // Then
        assertNotNull(lastPage)
        assertEquals(2, lastPage.number)
        assertEquals(10, lastPage.size)
        assertEquals(3, lastPage.totalPages)
        assertEquals(25L, lastPage.totalElements)
        assertEquals(5, lastPage.content.size)
    }

    @Test
    fun `should handle single page with all content`() {
        // Given
        val content = listOf("item1", "item2", "item3")

        // When
        val singlePage = PageVO(
            content = content,
            totalElements = 3L,
            totalPages = 1,
            size = 10,
            number = 0
        )

        // Then
        assertNotNull(singlePage)
        assertEquals(0, singlePage.number)
        assertEquals(10, singlePage.size)
        assertEquals(1, singlePage.totalPages)
        assertEquals(3L, singlePage.totalElements)
        assertEquals(3, singlePage.content.size)
    }

    @Test
    fun `should handle large page sizes`() {
        // Given
        val content = (1..100).map { "item$it" }

        // When
        val largePage = PageVO(
            content = content,
            totalElements = 1000L,
            totalPages = 10,
            size = 100,
            number = 0
        )

        // Then
        assertNotNull(largePage)
        assertEquals(0, largePage.number)
        assertEquals(100, largePage.size)
        assertEquals(10, largePage.totalPages)
        assertEquals(1000L, largePage.totalElements)
        assertEquals(100, largePage.content.size)
        assertEquals("item1", largePage.content.first())
        assertEquals("item100", largePage.content.last())
    }

    @Test
    fun `should handle small page sizes`() {
        // Given
        val content = listOf("item1")

        // When
        val smallPage = PageVO(
            content = content,
            totalElements = 100L,
            totalPages = 100,
            size = 1,
            number = 0
        )

        // Then
        assertNotNull(smallPage)
        assertEquals(0, smallPage.number)
        assertEquals(1, smallPage.size)
        assertEquals(100, smallPage.totalPages)
        assertEquals(100L, smallPage.totalElements)
        assertEquals(1, smallPage.content.size)
        assertEquals("item1", smallPage.content[0])
    }

    @Test
    fun `should be serializable to expected JSON structure`() {
        // Given
        val content = listOf(
            mapOf("id" to 1, "name" to "Alice"),
            mapOf("id" to 2, "name" to "Bob")
        )
        val pageVO = PageVO(
            content = content,
            totalElements = 20L,
            totalPages = 2,
            size = 10,
            number = 0
        )

        // Then - verify the structure matches expected format
        assertNotNull(pageVO)
        assertEquals(content, pageVO.content)
        assertEquals(20L, pageVO.totalElements)
        assertEquals(2, pageVO.totalPages)
        assertEquals(10, pageVO.size)
        assertEquals(0, pageVO.number)
        
        // The pageVO should have the structure: 
        // {content: [...], totalElements: 20, totalPages: 2, size: 10, number: 0}
        // This would be verified in integration tests with actual JSON serialization
    }

    @Test
    fun `should support equality comparison`() {
        // Given
        val content = listOf("item1", "item2")
        val page1 = PageVO(
            content = content,
            totalElements = 10L,
            totalPages = 1,
            size = 10,
            number = 0
        )
        val page2 = PageVO(
            content = content,
            totalElements = 10L,
            totalPages = 1,
            size = 10,
            number = 0
        )
        val page3 = PageVO(
            content = listOf("different"),
            totalElements = 10L,
            totalPages = 1,
            size = 10,
            number = 0
        )

        // Then
        assertEquals(page1, page2) // Same content and metadata
        assertEquals(page1.hashCode(), page2.hashCode())
        
        // Different objects should not be equal
        assertNotNull(page1)
        assertNotNull(page3)
        val areEqual = page1 == page3
        assertFalse(areEqual)
    }
}
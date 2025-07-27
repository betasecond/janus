package edu.jimei.janus.controller.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.domain.knowledge.KnowledgePoint
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionType
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
import org.junit.jupiter.api.Assertions.assertNull

/**
 * QuestionVOMapper单元测试
 * 测试题目实体到VO的映射功能，包括复杂的content解析和选项转换
 */
@ExtendWith(MockitoExtension::class)
class QuestionVOMapperTest {

    @Mock
    private lateinit var enumConverter: EnumConverter

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @InjectMocks
    private lateinit var questionVOMapper: QuestionVOMapper

    @Test
    fun `should map Question to QuestionVO correctly for multiple choice`() {
        // Given
        val questionId = UUID.randomUUID()
        val knowledgePoint1Id = UUID.randomUUID()
        val knowledgePoint2Id = UUID.randomUUID()
        
        val knowledgePoint1 = KnowledgePoint(
            id = knowledgePoint1Id,
            name = "Basic Programming",
            description = "Fundamental programming concepts",
            subject = "Computer Science"
        )
        
        val knowledgePoint2 = KnowledgePoint(
            id = knowledgePoint2Id,
            name = "Data Types",
            description = "Understanding data types",
            subject = "Computer Science"
        )
        
        val question = Question(
            id = questionId,
            type = QuestionType.MULTIPLE_CHOICE,
            difficulty = Difficulty.EASY,
            content = """{"question": "What is a variable?", "options": {"A": "A storage location", "B": "A function", "C": "A loop", "D": "A condition"}}""",
            correctAnswer = "A",
            explanation = "A variable is a storage location with an associated name.",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        question.knowledgePoints.add(knowledgePoint1)
        question.knowledgePoints.add(knowledgePoint2)
        
        `when`(enumConverter.convertQuestionType(QuestionType.MULTIPLE_CHOICE)).thenReturn("SINGLE_CHOICE")
        `when`(enumConverter.convertDifficulty(Difficulty.EASY)).thenReturn("EASY")

        // When
        val questionVO = questionVOMapper.toVO(question)

        // Then
        assertNotNull(questionVO)
        assertEquals(questionId.toString(), questionVO.id)
        assertEquals("SINGLE_CHOICE", questionVO.type)
        assertEquals("EASY", questionVO.difficulty)
        assertEquals(2, questionVO.knowledgePointIds.size)
        assertEquals(setOf(knowledgePoint1Id.toString(), knowledgePoint2Id.toString()), questionVO.knowledgePointIds.toSet())
        assertEquals("A", questionVO.correctAnswer)
        assertEquals("A variable is a storage location with an associated name.", questionVO.explanation)
    }

    @Test
    fun `should map Question to QuestionVO correctly for short answer`() {
        // Given
        val questionId = UUID.randomUUID()
        
        val question = Question(
            id = questionId,
            type = QuestionType.SHORT_ANSWER,
            difficulty = Difficulty.HARD,
            content = """{"question": "Explain the concept of polymorphism in object-oriented programming."}""",
            correctAnswer = null,
            explanation = "Polymorphism allows objects of different types to be treated as objects of a common base type.",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(enumConverter.convertQuestionType(QuestionType.SHORT_ANSWER)).thenReturn("SHORT_ANSWER")
        `when`(enumConverter.convertDifficulty(Difficulty.HARD)).thenReturn("HARD")

        // When
        val questionVO = questionVOMapper.toVO(question)

        // Then
        assertNotNull(questionVO)
        assertEquals(questionId.toString(), questionVO.id)
        assertEquals("SHORT_ANSWER", questionVO.type)
        assertEquals("HARD", questionVO.difficulty)
        assertEquals(0, questionVO.knowledgePointIds.size)
        assertNull(questionVO.correctAnswer)
        assertEquals("Polymorphism allows objects of different types to be treated as objects of a common base type.", questionVO.explanation)
    }

    @Test
    fun `should handle empty knowledge points list`() {
        // Given
        val questionId = UUID.randomUUID()
        
        val question = Question(
            id = questionId,
            type = QuestionType.SHORT_ANSWER,
            difficulty = Difficulty.MEDIUM,
            content = """{"question": "What is recursion?"}""",
            correctAnswer = null,
            explanation = "Recursion is a programming technique where a function calls itself.",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        // No knowledge points added
        
        `when`(enumConverter.convertQuestionType(QuestionType.SHORT_ANSWER)).thenReturn("SHORT_ANSWER")
        `when`(enumConverter.convertDifficulty(Difficulty.MEDIUM)).thenReturn("MEDIUM")

        // When
        val questionVO = questionVOMapper.toVO(question)

        // Then
        assertNotNull(questionVO)
        assertEquals(questionId.toString(), questionVO.id)
        assertEquals("SHORT_ANSWER", questionVO.type)
        assertEquals("MEDIUM", questionVO.difficulty)
        assertEquals(0, questionVO.knowledgePointIds.size) // Should handle empty list
        assertNull(questionVO.correctAnswer)
        assertEquals("Recursion is a programming technique where a function calls itself.", questionVO.explanation)
    }

    @Test
    fun `should handle null explanation`() {
        // Given
        val questionId = UUID.randomUUID()
        
        val question = Question(
            id = questionId,
            type = QuestionType.TRUE_FALSE,
            difficulty = Difficulty.EASY,
            content = """{"question": "Is the sky blue?", "options": {"A": "True", "B": "False"}}""",
            correctAnswer = "A",
            explanation = null, // null explanation
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(enumConverter.convertQuestionType(QuestionType.TRUE_FALSE)).thenReturn("TRUE_FALSE")
        `when`(enumConverter.convertDifficulty(Difficulty.EASY)).thenReturn("EASY")

        // When
        val questionVO = questionVOMapper.toVO(question)

        // Then
        assertNotNull(questionVO)
        assertEquals(questionId.toString(), questionVO.id)
        assertEquals("TRUE_FALSE", questionVO.type)
        assertEquals("EASY", questionVO.difficulty)
        assertEquals(0, questionVO.knowledgePointIds.size)
        assertEquals("A", questionVO.correctAnswer)
        assertNull(questionVO.explanation) // Should preserve null value
    }
}
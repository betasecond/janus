package edu.jimei.janus.controller.mapper

import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.domain.assignment.Assignment
import edu.jimei.janus.domain.assignment.AssignmentSubmission
import edu.jimei.janus.domain.assignment.SubmissionStatus
import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionType
import edu.jimei.janus.domain.user.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

/**
 * AssignmentVOMapper单元测试
 * 测试作业实体到VO的映射功能，包括关联字段转换和日期格式化
 */
@ExtendWith(MockitoExtension::class)
class AssignmentVOMapperTest {

    @Mock
    private lateinit var enumConverter: EnumConverter

    @InjectMocks
    private lateinit var assignmentVOMapper: AssignmentVOMapper

    @Test
    fun `should map Assignment to AssignmentVO correctly`() {
        // Given
        val assignmentId = UUID.randomUUID()
        val courseId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        val question1Id = UUID.randomUUID()
        val question2Id = UUID.randomUUID()
        
        val creator = User(
            id = creatorId,
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
            name = "Computer Science 101",
            description = "Introduction to Computer Science",
            teacher = creator,
            coverImageUrl = "http://example.com/course.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val question1 = Question(
            id = question1Id,
            type = QuestionType.MULTIPLE_CHOICE,
            difficulty = Difficulty.EASY,
            content = """{"question": "What is a variable?"}""",
            correctAnswer = "A",
            explanation = "A variable is a storage location.",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val question2 = Question(
            id = question2Id,
            type = QuestionType.TRUE_FALSE,
            difficulty = Difficulty.MEDIUM,
            content = """{"question": "Java is object-oriented."}""",
            correctAnswer = "True",
            explanation = "Java is indeed object-oriented.",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val dueDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59)
        val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
        
        val assignment = Assignment(
            id = assignmentId,
            title = "Programming Fundamentals Quiz",
            description = "Test your understanding of basic programming concepts",
            course = course,
            creator = creator,
            dueDate = dueDate,
            createdAt = createdAt,
            updatedAt = LocalDateTime.now()
        )
        
        assignment.questions.add(question1)
        assignment.questions.add(question2)

        // When
        val assignmentVO = assignmentVOMapper.toVO(assignment)

        // Then
        assertNotNull(assignmentVO)
        assertEquals(assignmentId.toString(), assignmentVO.id)
        assertEquals("Programming Fundamentals Quiz", assignmentVO.title)
        assertEquals("Test your understanding of basic programming concepts", assignmentVO.description)
        assertEquals(dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), assignmentVO.dueDate)
        assertEquals(courseId.toString(), assignmentVO.courseId)
        assertEquals(2, assignmentVO.questionIds.size)
        assertEquals(listOf(question1Id.toString(), question2Id.toString()), assignmentVO.questionIds)
        assertEquals(createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), assignmentVO.createdAt)
    }

    @Test
    fun `should handle null description and dueDate`() {
        // Given
        val assignmentId = UUID.randomUUID()
        val courseId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        
        val creator = User(
            id = creatorId,
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
            description = "Math course",
            teacher = creator,
            coverImageUrl = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val assignment = Assignment(
            id = assignmentId,
            title = "Math Quiz",
            description = null, // null description
            course = course,
            creator = creator,
            dueDate = null, // null due date
            createdAt = null, // null created at
            updatedAt = LocalDateTime.now()
        )

        // When
        val assignmentVO = assignmentVOMapper.toVO(assignment)

        // Then
        assertNotNull(assignmentVO)
        assertEquals(assignmentId.toString(), assignmentVO.id)
        assertEquals("Math Quiz", assignmentVO.title)
        assertEquals("", assignmentVO.description) // Should return empty string for null description
        assertNull(assignmentVO.dueDate) // Should preserve null for dueDate
        assertEquals(courseId.toString(), assignmentVO.courseId)
        assertEquals(0, assignmentVO.questionIds.size) // No questions
        assertEquals("", assignmentVO.createdAt) // Should return empty string for null createdAt
    }

    @Test
    fun `should map AssignmentSubmission to AssignmentSubmissionVO correctly`() {
        // Given
        val submissionId = UUID.randomUUID()
        val assignmentId = UUID.randomUUID()
        val studentId = UUID.randomUUID()
        
        val student = User(
            id = studentId,
            username = "student1",
            passwordHash = "hashedpassword",
            email = "student@example.com",
            displayName = "Student One",
            avatarUrl = "http://example.com/student.jpg",
            role = "student",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val creator = User(
            id = UUID.randomUUID(),
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
            id = UUID.randomUUID(),
            name = "Computer Science 101",
            description = "Introduction to Computer Science",
            teacher = creator,
            coverImageUrl = "http://example.com/course.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val assignment = Assignment(
            id = assignmentId,
            title = "Programming Quiz",
            description = "Test your programming skills",
            course = course,
            creator = creator,
            dueDate = LocalDateTime.now().plusDays(7),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val submittedAt = LocalDateTime.of(2024, 6, 15, 14, 30, 0)
        
        val submission = AssignmentSubmission(
            id = submissionId,
            assignment = assignment,
            student = student,
            submittedAt = submittedAt,
            status = SubmissionStatus.GRADED,
            score = BigDecimal("85.50")
        )
        
        `when`(enumConverter.convertSubmissionStatus(SubmissionStatus.GRADED)).thenReturn("GRADED")

        // When
        val submissionVO = assignmentVOMapper.toVO(submission)

        // Then
        assertNotNull(submissionVO)
        assertEquals(submissionId.toString(), submissionVO.id)
        assertEquals(assignmentId.toString(), submissionVO.assignmentId)
        assertEquals(studentId.toString(), submissionVO.studentId)
        assertEquals(0, submissionVO.answers.size) // No answers in this test
        assertEquals(BigDecimal("85.50"), submissionVO.score)
        assertEquals("GRADED", submissionVO.status)
        assertEquals(submittedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), submissionVO.submittedAt)
    }

    @Test
    fun `should handle null submittedAt in AssignmentSubmission`() {
        // Given
        val submissionId = UUID.randomUUID()
        val assignmentId = UUID.randomUUID()
        val studentId = UUID.randomUUID()
        
        val student = User(
            id = studentId,
            username = "student1",
            passwordHash = "hashedpassword",
            email = "student@example.com",
            displayName = "Student One",
            avatarUrl = "http://example.com/student.jpg",
            role = "student",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val creator = User(
            id = UUID.randomUUID(),
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
            id = UUID.randomUUID(),
            name = "Computer Science 101",
            description = "Introduction to Computer Science",
            teacher = creator,
            coverImageUrl = "http://example.com/course.jpg",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val assignment = Assignment(
            id = assignmentId,
            title = "Programming Quiz",
            description = "Test your programming skills",
            course = course,
            creator = creator,
            dueDate = LocalDateTime.now().plusDays(7),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val submission = AssignmentSubmission(
            id = submissionId,
            assignment = assignment,
            student = student,
            submittedAt = null, // null submitted at
            status = SubmissionStatus.SUBMITTED,
            score = null
        )
        
        `when`(enumConverter.convertSubmissionStatus(SubmissionStatus.SUBMITTED)).thenReturn("SUBMITTED")

        // When
        val submissionVO = assignmentVOMapper.toVO(submission)

        // Then
        assertNotNull(submissionVO)
        assertEquals(submissionId.toString(), submissionVO.id)
        assertEquals(assignmentId.toString(), submissionVO.assignmentId)
        assertEquals(studentId.toString(), submissionVO.studentId)
        assertEquals(0, submissionVO.answers.size)
        assertNull(submissionVO.score)
        assertEquals("SUBMITTED", submissionVO.status)
        assertNull(submissionVO.submittedAt) // Should preserve null value
    }
}
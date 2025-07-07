package edu.jimei.janus.controller.vo.common

import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.knowledge.KnowledgePoint
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionType
import edu.jimei.janus.domain.user.User
import java.time.LocalDateTime
import java.util.UUID

data class CourseVO(
    val id: UUID,
    val name: String,
    val description: String?,
    val coverImageUrl: String?,
    val teacher: UserVO,
    val studentCount: Long,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

fun Course.toVo(studentCount: Long = 0): CourseVO {
    return CourseVO(
        id = this.id ?: throw IllegalArgumentException("Course id cannot be null"),
        name = this.name,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        teacher = this.teacher.toVo(),
        studentCount = studentCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

data class UserVO(
    val id: UUID,
    val displayName: String?,
    val avatarUrl: String?,
    val role: String
)

fun User.toVo(): UserVO {
    return UserVO(
        id = this.id ?: throw IllegalArgumentException("User id cannot be null"),
        displayName = this.displayName,
        avatarUrl = this.avatarUrl,
        role = this.role
    )
}

data class KnowledgePointVO(
    val id: UUID,
    val name: String,
    val description: String?,
    val subject: String?
)

fun KnowledgePoint.toVo(): KnowledgePointVO {
    return KnowledgePointVO(
        id = this.id ?: throw IllegalArgumentException("KnowledgePoint id cannot be null"),
        name = this.name,
        description = this.description,
        subject = this.subject
    )
}

data class QuestionVO(
    val id: UUID,
    val type: QuestionType,
    val difficulty: Difficulty,
    val content: String,
    val explanation: String?,
    val knowledgePoints: Set<KnowledgePointVO>,
    val creator: UserVO?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

fun Question.toVo(): QuestionVO {
    return QuestionVO(
        id = this.id ?: throw IllegalArgumentException("Question id cannot be null"),
        type = this.type,
        difficulty = this.difficulty,
        content = this.content,
        explanation = this.explanation,
        knowledgePoints = this.knowledgePoints.map { it.toVo() }.toSet(),
        creator = this.creator?.toVo(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
} 
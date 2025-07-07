package edu.jimei.janus.controller.dto

import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionType
import java.time.LocalDateTime
import java.util.UUID

data class CreateQuestionDto(
    val type: QuestionType,
    val difficulty: Difficulty,
    val content: String,
    val correctAnswer: String?,
    val explanation: String?,
    val knowledgePointIds: Set<UUID>,
    val creatorId: UUID
)

data class UpdateQuestionDto(
    val type: QuestionType?,
    val difficulty: Difficulty?,
    val content: String?,
    val correctAnswer: String?,
    val explanation: String?,
    val knowledgePointIds: Set<UUID>?
)

data class QuestionDto(
    val id: UUID,
    val type: QuestionType,
    val difficulty: Difficulty,
    val content: String,
    val correctAnswer: String?,
    val explanation: String?,
    val knowledgePoints: Set<KnowledgePointDto>,
    val creator: UserDto?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class KnowledgePointDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val subject: String?
)

data class QuestionSearchDto(
    val type: QuestionType?,
    val difficulty: Difficulty?,
    val knowledgePointIds: List<UUID>?,
    val subject: String?,
    val creatorId: UUID?,
    val keyword: String?
)

// 扩展函数：Domain对象转DTO
fun Question.toDto(): QuestionDto {
    return QuestionDto(
        id = this.id ?: throw IllegalArgumentException("Question id cannot be null"),
        type = this.type,
        difficulty = this.difficulty,
        content = this.content,
        correctAnswer = this.correctAnswer,
        explanation = this.explanation,
        knowledgePoints = this.knowledgePoints.map { it.toDto() }.toSet(),
        creator = this.creator?.toDto(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun edu.jimei.janus.domain.knowledge.KnowledgePoint.toDto(): KnowledgePointDto {
    return KnowledgePointDto(
        id = this.id ?: throw IllegalArgumentException("KnowledgePoint id cannot be null"),
        name = this.name,
        description = this.description,
        subject = this.subject
    )
}

data class QuestionStatsDto(
    val total: Long,
    val byType: Map<QuestionType, Long>,
    val byDifficulty: Map<Difficulty, Long>
)

package edu.jimei.janus.domain.question

import edu.jimei.janus.domain.knowledge.KnowledgePoint
import edu.jimei.janus.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    SHORT_ANSWER,
    ESSAY
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

@Entity
@Table(name = "janus_questions")
@EntityListeners(AuditingEntityListener::class)
class Question(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var type: QuestionType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var difficulty: Difficulty,

    @Column(nullable = false, columnDefinition = "jsonb")
    var content: String, // JSON格式存储题目内容（题干、选项等）

    @Column(name = "correct_answer", columnDefinition = "jsonb")
    var correctAnswer: String?, // JSON格式存储正确答案

    @Column(columnDefinition = "TEXT")
    var explanation: String?, // 题目解析

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    var creator: User? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    // 多对多关系：题目和知识点
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "janus_question_knowledge_points",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "knowledge_point_id")]
    )
    var knowledgePoints: MutableSet<KnowledgePoint> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Question) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

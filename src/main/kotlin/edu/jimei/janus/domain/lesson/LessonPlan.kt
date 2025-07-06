package edu.jimei.janus.domain.lesson

import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class LessonPlanStatus {
    DRAFT,
    GENERATING,
    COMPLETED,
    FAILED
}

enum class ContentType {
    LECTURE,
    EXERCISE,
    NOTE,
    SUMMARY
}

@Entity
@Table(name = "janus_lesson_plans")
@EntityListeners(AuditingEntityListener::class)
class LessonPlan(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false, length = 255)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    var course: Course? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User,

    @Column(name = "source_document_url", length = 512)
    var sourceDocumentUrl: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: LessonPlanStatus = LessonPlanStatus.DRAFT,    @Column(name = "ai_model_used", length = 100)
    var aiModelUsed: String?,

    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    var aiPrompt: String?,

    @Column(name = "generation_duration_ms")
    var generationDurationMs: Long?,

    @Column(name = "error_message", columnDefinition = "TEXT")
    var errorMessage: String?,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    // 一对多关系：教学计划包含多个内容项
    @OneToMany(mappedBy = "lessonPlan", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderColumn(name = "\"order\"")
    var items: MutableList<LessonPlanItem> = mutableListOf()

    // 一对多关系：教学计划包含多个文档块（用于RAG）
    @OneToMany(mappedBy = "lessonPlan", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderColumn(name = "chunk_order")
    var documentChunks: MutableList<DocumentChunk> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LessonPlan) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

@Entity
@Table(name = "janus_lesson_plan_items")
@EntityListeners(AuditingEntityListener::class)
class LessonPlanItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_plan_id", nullable = false)
    var lessonPlan: LessonPlan,

    @Column(nullable = false, length = 255)
    var title: String,

    @Enumerated(EnumType.STRING)    @Column(name = "content_type", nullable = false, length = 50)
    var contentType: ContentType,

    @Column(columnDefinition = "TEXT")
    var content: String?,

    @Column(name = "\"order\"")
    var order: Int?,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LessonPlanItem) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

@Entity
@Table(name = "janus_document_chunks")
@EntityListeners(AuditingEntityListener::class)
class DocumentChunk(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)    @JoinColumn(name = "lesson_plan_id", nullable = false)
    var lessonPlan: LessonPlan,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(name = "chunk_order", nullable = false)
    var chunkOrder: Int,

    @Column(name = "vector_id", unique = true, length = 255)
    var vectorId: String?,

    @Column(columnDefinition = "jsonb")
    var metadata: String?,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DocumentChunk) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

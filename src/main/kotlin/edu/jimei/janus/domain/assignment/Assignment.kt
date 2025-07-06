package edu.jimei.janus.domain.assignment

import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "janus_assignments")
@EntityListeners(AuditingEntityListener::class)
class Assignment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false, length = 255)
    var title: String,

    @Lob
    var description: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    var course: Course,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User,

    @Column(name = "due_date")
    var dueDate: LocalDateTime?,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    // 多对多关系：作业和题目（通过assignment_questions表）
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "janus_assignment_questions",
        joinColumns = [JoinColumn(name = "assignment_id")],
        inverseJoinColumns = [JoinColumn(name = "question_id")]
    )
    @OrderColumn(name = "\"order\"") // 使用双引号因为order是保留字
    var questions: MutableList<Question> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Assignment) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

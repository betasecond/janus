package edu.jimei.janus.domain.assignment

import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

enum class SubmissionStatus {
    SUBMITTED,
    GRADING,
    GRADED
}

@Entity
@Table(name = "janus_assignment_submissions")
class AssignmentSubmission(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    var assignment: Assignment,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    var student: User,

    @Column(name = "submitted_at")
    var submittedAt: LocalDateTime? = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: SubmissionStatus = SubmissionStatus.SUBMITTED,

    @Column(precision = 5, scale = 2)
    var score: Double? = null
) {
    // 一对多关系：提交对应多个答案
    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var answers: MutableList<SubmissionAnswer> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssignmentSubmission) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

@Entity
@Table(name = "janus_submission_answers")
class SubmissionAnswer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    var submission: AssignmentSubmission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    var question: Question,

    @Column(columnDefinition = "jsonb")
    var answer: String?, // JSON格式存储学生答案

    @Column(name = "is_correct")
    var isCorrect: Boolean? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubmissionAnswer) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

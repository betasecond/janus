package edu.jimei.janus.controller.vo

import edu.jimei.janus.domain.lesson.ContentType
import edu.jimei.janus.domain.lesson.LessonPlan
import edu.jimei.janus.domain.lesson.LessonPlanItem
import edu.jimei.janus.domain.lesson.LessonPlanStatus
import java.util.*

// Corresponds to mockChapters in the frontend
data class ChapterVO(
    val id: UUID,
    val title: String,
    val order: Int,
    val contentType: ContentType,
    val content: String?,
    // In a real scenario, this might need a more complex logic
    // For now, we assume a chapter is completed if it exists.
    val isCompleted: Boolean 
)

// Corresponds to mockSyllabus in the frontend
data class SyllabusVO(
    val id: UUID,
    val courseId: UUID?,
    val name: String,
    val status: LessonPlanStatus,
    val chapters: List<ChapterVO>,
    val progress: Int // Progress calculation might be needed
)

fun LessonPlanItem.toChapterVo(): ChapterVO = ChapterVO(
    id = this.id!!,
    title = this.title,
    order = this.order ?: 0,
    contentType = this.contentType,
    content = this.content,
    isCompleted = true // Simplified logic
)

fun LessonPlan.toSyllabusVo(): SyllabusVO {
    val completedChapters = this.items.size
    val totalChapters = this.items.size // Simplified, could be a different value in a real case
    val progress = if (totalChapters > 0) (completedChapters * 100 / totalChapters) else 0

    return SyllabusVO(
        id = this.id!!,
        courseId = this.course?.id,
        name = this.name,
        status = this.status,
        // Filter out potential nulls added by JPA @OrderColumn before mapping
        chapters = this.items.filterNotNull().map { it.toChapterVo() }.sortedBy { it.order },
        progress = progress
    )
} 
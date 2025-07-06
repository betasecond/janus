package edu.jimei.janus.domain.lesson

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LessonPlanRepository : JpaRepository<LessonPlan, UUID> {
    fun findByCourseId(courseId: UUID): List<LessonPlan>
    
    fun findByCreatorId(creatorId: UUID): List<LessonPlan>
    
    fun findByStatus(status: LessonPlanStatus): List<LessonPlan>
    
    fun findByNameContainingIgnoreCase(name: String): List<LessonPlan>
    
    @Query("SELECT lp FROM LessonPlan lp WHERE lp.course IS NULL AND lp.creator.id = :creatorId")
    fun findTemplateLessonPlansByCreator(@Param("creatorId") creatorId: UUID): List<LessonPlan>
}

@Repository
interface LessonPlanItemRepository : JpaRepository<LessonPlanItem, UUID> {
    fun findByLessonPlanId(lessonPlanId: UUID): List<LessonPlanItem>
    
    fun findByLessonPlanIdOrderByOrder(lessonPlanId: UUID): List<LessonPlanItem>
    
    fun findByContentType(contentType: ContentType): List<LessonPlanItem>
}

@Repository
interface DocumentChunkRepository : JpaRepository<DocumentChunk, UUID> {
    fun findByLessonPlanId(lessonPlanId: UUID): List<DocumentChunk>
    
    fun findByLessonPlanIdOrderByChunkOrder(lessonPlanId: UUID): List<DocumentChunk>
    
    fun findByVectorId(vectorId: String): DocumentChunk?
}

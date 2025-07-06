package edu.jimei.janus.domain.knowledge

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface KnowledgePointRepository : JpaRepository<KnowledgePoint, UUID> {
    fun findBySubject(subject: String): List<KnowledgePoint>
    
    fun findByParentId(parentId: UUID): List<KnowledgePoint>
    
    fun findByParentIsNull(): List<KnowledgePoint>
    
    fun findByNameContainingIgnoreCase(name: String): List<KnowledgePoint>
    
    @Query("SELECT k FROM KnowledgePoint k WHERE k.parent IS NULL AND k.subject = :subject")
    fun findRootKnowledgePointsBySubject(@Param("subject") subject: String): List<KnowledgePoint>
}

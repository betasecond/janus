package edu.jimei.janus.domain.question

import edu.jimei.janus.domain.knowledge.KnowledgePoint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface QuestionRepository : JpaRepository<Question, UUID> {
    fun findByType(type: QuestionType): List<Question>
    
    fun findByDifficulty(difficulty: Difficulty): List<Question>
    
    fun findByCreatorId(creatorId: UUID): List<Question>
    
    @Query("SELECT q FROM Question q JOIN q.knowledgePoints kp WHERE kp = :knowledgePoint")
    fun findByKnowledgePointsContaining(@Param("knowledgePoint") knowledgePoint: KnowledgePoint): List<Question>
    
    @Query("SELECT q FROM Question q JOIN q.knowledgePoints kp WHERE kp IN :knowledgePoints AND q.difficulty = :difficulty")
    fun findByKnowledgePointsInAndDifficulty(
        @Param("knowledgePoints") knowledgePoints: List<KnowledgePoint>, 
        @Param("difficulty") difficulty: Difficulty
    ): List<Question>
    
    @Query("SELECT q FROM Question q JOIN q.knowledgePoints kp WHERE kp.subject = :subject")
    fun findByKnowledgePointSubject(@Param("subject") subject: String): List<Question>
    
    fun findByTypeAndDifficulty(type: QuestionType, difficulty: Difficulty): List<Question>
}

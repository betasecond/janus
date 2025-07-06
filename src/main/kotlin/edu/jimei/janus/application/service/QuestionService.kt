package edu.jimei.janus.application.service

import edu.jimei.janus.domain.knowledge.KnowledgePoint
import edu.jimei.janus.domain.knowledge.KnowledgePointRepository
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionRepository
import edu.jimei.janus.domain.question.QuestionType
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val knowledgePointRepository: KnowledgePointRepository,
    private val userRepository: UserRepository
) {

    fun createQuestion(
        type: QuestionType,
        difficulty: Difficulty,
        content: String,
        correctAnswer: String?,
        explanation: String?,
        knowledgePointIds: Set<UUID>,
        creatorId: UUID
    ): Question {
        val creator = userRepository.findById(creatorId).orElseThrow {
            IllegalArgumentException("Creator with ID $creatorId not found")
        }

        val knowledgePoints = knowledgePointRepository.findAllById(knowledgePointIds).toMutableSet()
        if (knowledgePoints.size != knowledgePointIds.size) {
            throw IllegalArgumentException("Some knowledge points not found")
        }

        val question = Question(
            type = type,
            difficulty = difficulty,
            content = content,
            correctAnswer = correctAnswer,
            explanation = explanation,
            creator = creator
        )
        question.knowledgePoints = knowledgePoints

        return questionRepository.save(question)
    }

    fun findById(questionId: UUID): Question {
        return questionRepository.findById(questionId).orElseThrow {
            IllegalArgumentException("Question with ID $questionId not found")
        }
    }

    fun updateQuestion(
        questionId: UUID,
        type: QuestionType?,
        difficulty: Difficulty?,
        content: String?,
        correctAnswer: String?,
        explanation: String?,
        knowledgePointIds: Set<UUID>?
    ): Question {
        val question = findById(questionId)

        type?.let { question.type = it }
        difficulty?.let { question.difficulty = it }
        content?.let { question.content = it }
        correctAnswer?.let { question.correctAnswer = it }
        explanation?.let { question.explanation = it }
        
        knowledgePointIds?.let { ids ->
            val knowledgePoints = knowledgePointRepository.findAllById(ids).toMutableSet()
            if (knowledgePoints.size != ids.size) {
                throw IllegalArgumentException("Some knowledge points not found")
            }
            question.knowledgePoints = knowledgePoints
        }

        return questionRepository.save(question)
    }

    fun findByType(type: QuestionType): List<Question> {
        return questionRepository.findByType(type)
    }

    fun findByDifficulty(difficulty: Difficulty): List<Question> {
        return questionRepository.findByDifficulty(difficulty)
    }

    fun findByCreator(creatorId: UUID): List<Question> {
        return questionRepository.findByCreatorId(creatorId)
    }

    fun findByKnowledgePoint(knowledgePointId: UUID): List<Question> {
        val knowledgePoint = knowledgePointRepository.findById(knowledgePointId).orElseThrow {
            IllegalArgumentException("Knowledge point with ID $knowledgePointId not found")
        }
        return questionRepository.findByKnowledgePointsContaining(knowledgePoint)
    }

    fun findByKnowledgePointsAndDifficulty(knowledgePointIds: List<UUID>, difficulty: Difficulty): List<Question> {
        val knowledgePoints = knowledgePointRepository.findAllById(knowledgePointIds)
        return questionRepository.findByKnowledgePointsInAndDifficulty(knowledgePoints, difficulty)
    }

    fun findBySubject(subject: String): List<Question> {
        return questionRepository.findByKnowledgePointSubject(subject)
    }

    fun findByTypeAndDifficulty(type: QuestionType, difficulty: Difficulty): List<Question> {
        return questionRepository.findByTypeAndDifficulty(type, difficulty)
    }

    fun deleteQuestion(questionId: UUID) {
        val question = findById(questionId)
        questionRepository.delete(question)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Question> {
        return questionRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Question> {
        return questionRepository.findAll(pageable)
    }
}

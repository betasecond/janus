package edu.jimei.janus.controller

import edu.jimei.janus.application.service.QuestionService
import edu.jimei.janus.controller.dto.CreateQuestionDto
import edu.jimei.janus.controller.dto.QuestionSearchDto
import edu.jimei.janus.controller.dto.UpdateQuestionDto
import edu.jimei.janus.controller.vo.QuestionStatsVO
import edu.jimei.janus.controller.vo.common.PageVO
import edu.jimei.janus.controller.vo.common.QuestionVO
import edu.jimei.janus.controller.vo.common.toVo
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.QuestionType
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = ["*"])
class QuestionController(
    private val questionService: QuestionService
) {

    @PostMapping
    fun createQuestion(@RequestBody createDto: CreateQuestionDto): ResponseEntity<QuestionVO> {
        val question = questionService.createQuestion(
            type = createDto.type,
            difficulty = createDto.difficulty,
            content = createDto.content,
            correctAnswer = createDto.correctAnswer,
            explanation = createDto.explanation,
            knowledgePointIds = createDto.knowledgePointIds,
            creatorId = createDto.creatorId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(question.toVo())
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: UUID): ResponseEntity<QuestionVO> {
        val question = questionService.findById(id)
        return ResponseEntity.ok(question.toVo())
    }

    @GetMapping
    fun getAllQuestions(
        @RequestParam(required = false) type: QuestionType?,
        @RequestParam(required = false) difficulty: Difficulty?,
        @RequestParam(required = false) creatorId: UUID?,
        @RequestParam(required = false) knowledgePointId: UUID?,
        @RequestParam(required = false) subject: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageVO<QuestionVO>> {
        val questions = when {
            type != null && difficulty != null -> questionService.findByTypeAndDifficulty(type, difficulty)
            type != null -> questionService.findByType(type)
            difficulty != null -> questionService.findByDifficulty(difficulty)
            creatorId != null -> questionService.findByCreator(creatorId)
            knowledgePointId != null -> questionService.findByKnowledgePoint(knowledgePointId)
            subject != null -> questionService.findBySubject(subject)
            else -> {
                val pageable = PageRequest.of(page, size)
                val questionPage = questionService.findAll(pageable)
                return ResponseEntity.ok(questionPage.toVo { it.toVo() })
            }
        }

        val questionVos = questions.map { it.toVo() }
        val pageVo = PageVO(
            content = questionVos,
            totalElements = questionVos.size.toLong(),
            totalPages = 1,
            size = questionVos.size,
            number = 0
        )
        return ResponseEntity.ok(pageVo)
    }

    @PostMapping("/search")
    fun searchQuestions(@RequestBody searchDto: QuestionSearchDto): ResponseEntity<List<QuestionVO>> {
        val questions = when {
            searchDto.knowledgePointIds != null && searchDto.difficulty != null -> 
                questionService.findByKnowledgePointsAndDifficulty(searchDto.knowledgePointIds, searchDto.difficulty)
            searchDto.type != null && searchDto.difficulty != null -> 
                questionService.findByTypeAndDifficulty(searchDto.type, searchDto.difficulty)
            searchDto.type != null -> questionService.findByType(searchDto.type)
            searchDto.difficulty != null -> questionService.findByDifficulty(searchDto.difficulty)
            searchDto.creatorId != null -> questionService.findByCreator(searchDto.creatorId)
            searchDto.subject != null -> questionService.findBySubject(searchDto.subject)
            else -> questionService.findAll()
        }

        val questionVos = questions.map { it.toVo() }
        return ResponseEntity.ok(questionVos)
    }

    @PutMapping("/{id}")
    fun updateQuestion(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateQuestionDto
    ): ResponseEntity<QuestionVO> {
        val updatedQuestion = questionService.updateQuestion(
            questionId = id,
            type = updateDto.type,
            difficulty = updateDto.difficulty,
            content = updateDto.content,
            correctAnswer = updateDto.correctAnswer,
            explanation = updateDto.explanation,
            knowledgePointIds = updateDto.knowledgePointIds
        )
        
        return ResponseEntity.ok(updatedQuestion.toVo())
    }

    @DeleteMapping("/{id}")
    fun deleteQuestion(@PathVariable id: UUID): ResponseEntity<Void> {
        questionService.deleteQuestion(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/stats")
    fun getQuestionStats(): ResponseEntity<QuestionStatsVO> {
        val allQuestions = questionService.findAll()
        
        val statsByType = QuestionType.values().associateWith { type ->
            allQuestions.count { it.type == type }
        }
        
        val statsByDifficulty = Difficulty.values().associateWith { difficulty ->
            allQuestions.count { it.difficulty == difficulty }
        }
        
        val stats = QuestionStatsVO(
            total = allQuestions.size,
            byType = statsByType,
            byDifficulty = statsByDifficulty
        )
        
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/types")
    fun getQuestionTypes(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(QuestionType.values().map { it.name })
    }

    @GetMapping("/difficulties")
    fun getDifficulties(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(Difficulty.values().map { it.name })
    }

    // 异常处理
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "error" to "Bad Request",
                "message" to (ex.message ?: "Invalid request")
            )
        )
    }
}

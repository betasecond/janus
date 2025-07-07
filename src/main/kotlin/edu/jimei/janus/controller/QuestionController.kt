package edu.jimei.janus.controller

import edu.jimei.janus.application.service.QuestionService
import edu.jimei.janus.controller.dto.*
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
    fun createQuestion(@RequestBody createDto: CreateQuestionDto): ResponseEntity<QuestionDto> {
        val question = questionService.createQuestion(
            type = createDto.type,
            difficulty = createDto.difficulty,
            content = createDto.content,
            correctAnswer = createDto.correctAnswer,
            explanation = createDto.explanation,
            knowledgePointIds = createDto.knowledgePointIds,
            creatorId = createDto.creatorId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(question.toDto())
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: UUID): ResponseEntity<QuestionDto> {
        val question = questionService.findById(id)
        return ResponseEntity.ok(question.toDto())
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
    ): ResponseEntity<*> {
        if (type == null && difficulty == null && creatorId == null && knowledgePointId == null && subject == null) {
            val pageable = PageRequest.of(page, size)
            val questionPage = questionService.findAll(pageable)
            return ResponseEntity.ok(questionPage.toDto { it.toDto() })
        }

        val questions = when {
            type != null && difficulty != null -> questionService.findByTypeAndDifficulty(type, difficulty)
            type != null -> questionService.findByType(type)
            difficulty != null -> questionService.findByDifficulty(difficulty)
            creatorId != null -> questionService.findByCreator(creatorId)
            knowledgePointId != null -> questionService.findByKnowledgePoint(knowledgePointId)
            subject != null -> questionService.findBySubject(subject)
            else -> emptyList()
        }

        val questionDtos = questions.map { it.toDto() }
        return ResponseEntity.ok(PageDto(
            content = questionDtos,
            totalElements = questionDtos.size.toLong(),
            totalPages = 1,
            size = questionDtos.size,
            number = 0
        ))
    }

    @PostMapping("/search")
    fun searchQuestions(@RequestBody searchDto: QuestionSearchDto): ResponseEntity<List<QuestionDto>> {
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

        val questionDtos = questions.map { it.toDto() }
        return ResponseEntity.ok(questionDtos)
    }

    @PutMapping("/{id}")
    fun updateQuestion(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateQuestionDto
    ): ResponseEntity<QuestionDto> {
        val updatedQuestion = questionService.updateQuestion(
            questionId = id,
            type = updateDto.type,
            difficulty = updateDto.difficulty,
            content = updateDto.content,
            correctAnswer = updateDto.correctAnswer,
            explanation = updateDto.explanation,
            knowledgePointIds = updateDto.knowledgePointIds
        )
        
        return ResponseEntity.ok(updatedQuestion.toDto())
    }

    @DeleteMapping("/{id}")
    fun deleteQuestion(@PathVariable id: UUID): ResponseEntity<Void> {
        questionService.deleteQuestion(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/stats")
    fun getQuestionStats(): ResponseEntity<QuestionStatsDto> {
        val allQuestions = questionService.findAll()

        val statsByType = QuestionType.values().associateWith { type ->
            allQuestions.count { it.type == type }.toLong()
        }

        val statsByDifficulty = Difficulty.values().associateWith { difficulty ->
            allQuestions.count { it.difficulty == difficulty }.toLong()
        }

        val stats = QuestionStatsDto(
            total = allQuestions.size.toLong(),
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

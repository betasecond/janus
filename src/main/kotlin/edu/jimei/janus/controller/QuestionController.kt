package edu.jimei.janus.controller

import edu.jimei.janus.application.service.QuestionService
import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.common.PageVO
import edu.jimei.janus.controller.dto.CreateQuestionDto
import edu.jimei.janus.controller.dto.QuestionQuery
import edu.jimei.janus.controller.dto.QuestionSearchDto
import edu.jimei.janus.controller.dto.UpdateQuestionDto
import edu.jimei.janus.controller.mapper.QuestionVOMapper
import edu.jimei.janus.controller.vo.QuestionStatsVO
import edu.jimei.janus.controller.vo.QuestionVO
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.QuestionType
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/questions")
class QuestionController(
    private val questionService: QuestionService,
    private val questionVOMapper: QuestionVOMapper
) {

    @PostMapping
    fun createQuestion(@RequestBody createDto: CreateQuestionDto): ResponseEntity<ApiResponse<QuestionVO>> {
        val question = questionService.createQuestion(
            type = createDto.type,
            difficulty = createDto.difficulty,
            content = createDto.content,
            correctAnswer = createDto.correctAnswer,
            explanation = createDto.explanation,
            knowledgePointIds = createDto.knowledgePointIds,
            creatorId = createDto.creatorId
        )

        val questionVO = questionVOMapper.toVO(question)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = questionVO))
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: UUID): ResponseEntity<ApiResponse<QuestionVO>> {
        val question = questionService.findById(id)
        val questionVO = questionVOMapper.toVO(question)
        return ResponseEntity.ok(ApiResponse(data = questionVO))
    }

    @GetMapping
    fun getAllQuestions(
        @ModelAttribute query: QuestionQuery,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageVO<QuestionVO>>> {
        val pageable = PageRequest.of(page, size)
        val questionPage = questionService.searchQuestions(query, pageable)
        
        val questionVOs = questionPage.content.map { questionVOMapper.toVO(it) }
        val pageVO = PageVO(
            content = questionVOs,
            totalElements = questionPage.totalElements,
            totalPages = questionPage.totalPages,
            size = questionPage.size,
            number = questionPage.number
        )
        
        return ResponseEntity.ok(ApiResponse(data = pageVO))
    }

    @PostMapping("/search")
    fun searchQuestions(@RequestBody searchDto: QuestionSearchDto): ResponseEntity<ApiResponse<List<QuestionVO>>> {
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

        val questionVOs = questions.map { questionVOMapper.toVO(it) }
        return ResponseEntity.ok(ApiResponse(data = questionVOs))
    }

    @PutMapping("/{id}")
    fun updateQuestion(
        @PathVariable id: UUID,
        @RequestBody updateDto: UpdateQuestionDto
    ): ResponseEntity<ApiResponse<QuestionVO>> {
        val updatedQuestion = questionService.updateQuestion(
            questionId = id,
            type = updateDto.type,
            difficulty = updateDto.difficulty,
            content = updateDto.content,
            correctAnswer = updateDto.correctAnswer,
            explanation = updateDto.explanation,
            knowledgePointIds = updateDto.knowledgePointIds
        )

        val questionVO = questionVOMapper.toVO(updatedQuestion)
        return ResponseEntity.ok(ApiResponse(data = questionVO))
    }

    @DeleteMapping("/{id}")
    fun deleteQuestion(@PathVariable id: UUID): ResponseEntity<ApiResponse<String>> {
        questionService.deleteQuestion(id)
        return ResponseEntity.ok(ApiResponse(data = "Question deleted successfully"))
    }

    @GetMapping("/stats")
    fun getQuestionStats(): ResponseEntity<ApiResponse<QuestionStatsVO>> {
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

        return ResponseEntity.ok(ApiResponse(data = stats))
    }

    @GetMapping("/types")
    fun getQuestionTypes(): ResponseEntity<ApiResponse<List<String>>> {
        val types = QuestionType.values().map { it.name }
        return ResponseEntity.ok(ApiResponse(data = types))
    }

    @GetMapping("/difficulties")
    fun getDifficulties(): ResponseEntity<ApiResponse<List<String>>> {
        val difficulties = Difficulty.values().map { it.name }
        return ResponseEntity.ok(ApiResponse(data = difficulties))
    }


}

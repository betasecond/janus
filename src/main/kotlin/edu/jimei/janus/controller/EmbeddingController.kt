package edu.jimei.janus.controller

import edu.jimei.janus.application.service.EmbeddingService
import edu.jimei.janus.controller.dto.StatusResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class EmbeddingRequest(val message: String)

data class EmbeddingOutputDto(
    val index: Int,
    val embedding: List<Double>
)

data class EmbeddingApiResponseDto(
    val data: List<EmbeddingOutputDto>
)

@RestController
@RequestMapping("/ai")
class EmbeddingController(
    private val embeddingService: EmbeddingService,
    private val embeddingModel: EmbeddingModel
) {

    @PostMapping("/embedding/store")
    @Operation(
        summary = "Embed and store a message", responses = [
            ApiResponse(
                responseCode = "200", description = "Embedding stored successfully", content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = StatusResponseDto::class))
                ]
            )
        ]
    )
    fun embedAndStore(@RequestBody request: EmbeddingRequest): ResponseEntity<StatusResponseDto> {
        embeddingService.embedAndStore(request.message)
        val response = StatusResponseDto("success", "Embedding stored successfully.")
        return ResponseEntity.ok(response)
    }

//    @GetMapping("/embedding/similar")
//    fun findSimilar(@RequestParam("message") message: String): ResponseEntity<List<String>> {
//        val similarDocuments = embeddingService.findSimilar(message)
//        return ResponseEntity.ok(similarDocuments.map { it.content })
//    }

    @PostMapping("/embedding")
    @Operation(
        summary = "Embed a message", responses = [
            ApiResponse(
                responseCode = "200", description = "Embedding created successfully", content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = EmbeddingApiResponseDto::class))
                ]
            )
        ]
    )
    fun embed(@RequestBody request: EmbeddingRequest): EmbeddingApiResponseDto {
        val embeddingResponse = embeddingModel.embedForResponse(listOf(request.message))
        val embeddingOutputs = embeddingResponse.results.map {
            EmbeddingOutputDto(index = it.index, embedding = it.output)
        }
        return EmbeddingApiResponseDto(data = embeddingOutputs)
    }
}
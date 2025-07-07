package edu.jimei.janus.controller

import edu.jimei.janus.application.service.EmbeddingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


data class EmbeddingRequest(val message: String)

@RestController
@RequestMapping("/ai")
class EmbeddingController(
    private val embeddingService: EmbeddingService,
    private val embeddingModel: EmbeddingModel
) {

    @PostMapping("/embedding/store")
    @Operation(summary = "Embed and store a message", responses = [
        ApiResponse(responseCode = "200", description = "Embedding stored successfully", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = Map::class))
        ])
    ])
    fun embedAndStore(@RequestBody request: EmbeddingRequest): ResponseEntity<Map<String, String>> {
        embeddingService.embedAndStore(request.message)
        return ResponseEntity.ok(mapOf("status" to "success", "message" to "Embedding stored successfully."))
    }

//    @GetMapping("/embedding/similar")
//    fun findSimilar(@RequestParam("message") message: String): ResponseEntity<List<String>> {
//        val similarDocuments = embeddingService.findSimilar(message)
//        return ResponseEntity.ok(similarDocuments.map { it.content })
//    }

    @PostMapping("/embedding")
    @Operation(summary = "Embed a message", responses = [
        ApiResponse(responseCode = "200", description = "Embedding created successfully", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = Map::class))
        ])
    ])
    fun embed(@RequestBody request: EmbeddingRequest): Map<String, EmbeddingResponse> {
        val embeddingResponse = embeddingModel.embedForResponse(listOf(request.message))
        return mapOf("embedding" to embeddingResponse)
    }
}
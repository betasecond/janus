package edu.jimei.janus.controller

import edu.jimei.janus.application.service.EmbeddingService
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
class EmbeddingController(private val embeddingService: EmbeddingService) {

    @PostMapping("/embedding")
    fun embedAndStore(@RequestBody request: EmbeddingRequest): ResponseEntity<Map<String, String>> {
        embeddingService.embedAndStore(request.message)
        return ResponseEntity.ok(mapOf("status" to "success", "message" to "Embedding stored successfully."))
    }

    @GetMapping("/embedding/similar")
    fun findSimilar(@RequestParam("message") message: String): ResponseEntity<List<String>> {
        val similarDocuments = embeddingService.findSimilar(message)
        return ResponseEntity.ok(similarDocuments?.map { it.formattedContent })
    }
}
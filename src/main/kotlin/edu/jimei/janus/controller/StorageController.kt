package edu.jimei.janus.controller

import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.controller.dto.StorageObjectDto
import edu.jimei.janus.controller.dto.toDto
import edu.jimei.janus.infrastructure.pulsar.FileProcessingProducer
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/storage")
class StorageController(
    private val ossService: OssService,
    private val fileProcessingProducer: FileProcessingProducer
) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Upload a file", responses = [
        ApiResponse(responseCode = "200", description = "File uploaded successfully", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = StorageObjectDto::class))
        ])
    ])
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("uploaderId") uploaderId: UUID
    ): ResponseEntity<StorageObjectDto> {
        val storageObject = ossService.upload(file, uploaderId)
        return ResponseEntity.ok(storageObject.toDto())
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file details by ID", responses = [
        ApiResponse(responseCode = "200", description = "File details found", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = StorageObjectDto::class))
        ]),
        ApiResponse(responseCode = "404", description = "File not found", content = [Content()])
    ])
    fun getFileDetails(@PathVariable id: UUID): ResponseEntity<StorageObjectDto> {
        val storageObject = ossService.findById(id)
        return if (storageObject != null) {
            ResponseEntity.ok(storageObject.toDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/embed")
    @Operation(summary = "Request to embed a file", responses = [
        ApiResponse(responseCode = "222", description = "File processing request accepted", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = Map::class))
        ])
    ])
    fun embedFile(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        fileProcessingProducer.sendFileProcessingRequest(id)
        return ResponseEntity.accepted().body(mapOf("status" to "accepted", "message" to "File processing request accepted and queued."))
    }
} 
package edu.jimei.janus.controller

import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.controller.dto.StorageObjectDto
import edu.jimei.janus.controller.dto.toDto
import edu.jimei.janus.infrastructure.pulsar.FileProcessingProducer
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

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("uploaderId") uploaderId: UUID
    ): ResponseEntity<StorageObjectDto> {
        val storageObject = ossService.upload(file, uploaderId)
        return ResponseEntity.ok(storageObject.toDto())
    }

    @GetMapping("/{id}")
    fun getFileDetails(@PathVariable id: UUID): ResponseEntity<StorageObjectDto> {
        val storageObject = ossService.findById(id)
        return if (storageObject != null) {
            ResponseEntity.ok(storageObject.toDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/embed")
    fun embedFile(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        fileProcessingProducer.sendFileProcessingRequest(id)
        return ResponseEntity.accepted().body(mapOf("status" to "accepted", "message" to "File processing request accepted and queued."))
    }
} 
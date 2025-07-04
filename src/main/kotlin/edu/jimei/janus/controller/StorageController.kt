package edu.jimei.janus.controller

import edu.jimei.janus.application.service.FileProcessService
import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.controller.dto.StorageObjectDto
import edu.jimei.janus.controller.dto.toDto
import edu.jimei.janus.domain.storage.StorageObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/storage")
class StorageController(
    private val ossService: OssService,
    private val fileProcessService: FileProcessService
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
        fileProcessService.processFile(id)
        return ResponseEntity.ok(mapOf("status" to "success", "message" to "File processing started."))
    }
} 
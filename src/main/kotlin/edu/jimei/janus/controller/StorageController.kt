package edu.jimei.janus.controller

import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.controller.vo.StorageObjectVO
import edu.jimei.janus.controller.vo.common.StatusVO
import edu.jimei.janus.controller.vo.toVo
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
    ): ResponseEntity<StorageObjectVO> {
        val storageObject = ossService.upload(file, uploaderId)
        return ResponseEntity.ok(storageObject.toVo())
    }

    @GetMapping("/{id}")
    fun getFileDetails(@PathVariable id: UUID): ResponseEntity<StorageObjectVO> {
        val storageObject = ossService.findById(id)
        return if (storageObject != null) {
            ResponseEntity.ok(storageObject.toVo())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/embed")
    fun embedFile(@PathVariable id: UUID): ResponseEntity<StatusVO> {
        fileProcessingProducer.sendFileProcessingRequest(id)
        return ResponseEntity.accepted().body(StatusVO(status = "accepted", message = "File processing request accepted and queued."))
    }
} 
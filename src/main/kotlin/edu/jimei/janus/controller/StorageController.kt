package edu.jimei.janus.controller

import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.application.service.StorageService
import edu.jimei.janus.controller.vo.ResourceVO
import edu.jimei.janus.controller.vo.StorageObjectVO
import edu.jimei.janus.controller.vo.common.PageVO
import edu.jimei.janus.controller.vo.common.StatusVO
import edu.jimei.janus.controller.vo.common.toVo
import edu.jimei.janus.controller.vo.toResourceVo
import edu.jimei.janus.controller.vo.toVo
import edu.jimei.janus.domain.user.UserRepository
import edu.jimei.janus.infrastructure.pulsar.FileProcessingProducer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/storage")
class StorageController(
    private val ossService: OssService,
    private val fileProcessingProducer: FileProcessingProducer,
    private val storageService: StorageService,
    private val userRepository: UserRepository
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

    @GetMapping
    fun getFiles(
        // FIXME: This is a temporary solution for identifying the user.
        // In a real-world application, user information should be obtained from a security context (e.g., Spring Security's AuthenticationPrincipal).
        // This endpoint needs to be secured properly.
        @RequestParam currentUserId: UUID,
        @RequestParam(required = false) uploaderId: UUID?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageVO<StorageObjectVO>> {
        val currentUser = userRepository.findById(currentUserId)
            .orElseThrow { IllegalArgumentException("User with id $currentUserId not found") }

        val filesPage = storageService.getFiles(uploaderId, keyword, page, size, currentUser)
        return ResponseEntity.ok(filesPage.toVo { it.toVo() })
    }
} 
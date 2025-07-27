package edu.jimei.janus.controller

import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.application.service.StorageService
import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.common.PageVO
import edu.jimei.janus.controller.mapper.StorageObjectVOMapper
import edu.jimei.janus.controller.vo.StorageObjectVO
import edu.jimei.janus.controller.vo.common.StatusVO
import edu.jimei.janus.domain.user.UserRepository
import edu.jimei.janus.infrastructure.pulsar.FileProcessingProducer
import org.springframework.data.domain.Page
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
    private val userRepository: UserRepository,
    private val storageObjectVOMapper: StorageObjectVOMapper
) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("uploaderId") uploaderId: UUID
    ): ResponseEntity<ApiResponse<StorageObjectVO>> {
        val storageObject = ossService.upload(file, uploaderId)
        val storageObjectVO = storageObjectVOMapper.toVO(storageObject)
        return ResponseEntity.ok(ApiResponse(data = storageObjectVO))
    }

    @GetMapping("/{id}")
    fun getFileDetails(@PathVariable id: UUID): ResponseEntity<ApiResponse<StorageObjectVO>> {
        val storageObject = ossService.findById(id)
        return if (storageObject != null) {
            val storageObjectVO = storageObjectVOMapper.toVO(storageObject)
            ResponseEntity.ok(ApiResponse(data = storageObjectVO))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/embed")
    fun embedFile(@PathVariable id: UUID): ResponseEntity<ApiResponse<StatusVO>> {
        fileProcessingProducer.sendFileProcessingRequest(id)
        val statusVO = StatusVO(status = "accepted", message = "File processing request accepted and queued.")
        return ResponseEntity.accepted().body(ApiResponse(data = statusVO))
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
    ): ResponseEntity<ApiResponse<PageVO<StorageObjectVO>>> {
        val currentUser = userRepository.findById(currentUserId)
            .orElseThrow { IllegalArgumentException("User with id $currentUserId not found") }

        val filesPage = storageService.getFiles(uploaderId, keyword, page, size, currentUser)
        val pageVO = convertToPageVO(filesPage)
        return ResponseEntity.ok(ApiResponse(data = pageVO))
    }
    
    /**
     * 将Spring Data的Page对象转换为PageVO
     */
    private fun convertToPageVO(page: Page<edu.jimei.janus.domain.storage.StorageObject>): PageVO<StorageObjectVO> {
        val content = page.content.map { storageObjectVOMapper.toVO(it) }
        return PageVO(
            content = content,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            size = page.size,
            number = page.number
        )
    }
} 
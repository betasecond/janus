package edu.jimei.janus.controller

import edu.jimei.janus.application.service.StorageService
import edu.jimei.janus.controller.vo.ResourceVO
import edu.jimei.janus.controller.vo.toResourceVo
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/resources")
class ResourceController(
    private val storageService: StorageService,
    private val userRepository: UserRepository
) {

    /**
     * Retrieves a list of resources in a format suitable for the frontend resource management page.
     * This acts as a facade over the storage objects, presenting them in a more user-friendly manner.
     */
    @GetMapping
    fun getResources(
        // FIXME: This also needs proper security handling.
        @RequestParam currentUserId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<ResourceVO>> {
        val currentUser = userRepository.findById(currentUserId)
            .orElseThrow { IllegalArgumentException("User with id $currentUserId not found") }

        // We can reuse the existing service method. We're fetching all files for now.
        val filesPage = storageService.getFiles(null, null, page, size, currentUser)
        val resourceVos = filesPage.content.map { it.toResourceVo() }
        return ResponseEntity.ok(resourceVos)
    }
} 
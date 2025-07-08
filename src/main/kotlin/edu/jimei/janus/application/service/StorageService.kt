package edu.jimei.janus.application.service

import edu.jimei.janus.domain.storage.StorageObject
import edu.jimei.janus.domain.storage.StorageObjectRepository
import edu.jimei.janus.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*

@Service
class StorageService(
    private val storageObjectRepository: StorageObjectRepository
) {
    fun getFiles(
        uploaderId: UUID?,
        keyword: String?,
        page: Int,
        size: Int,
        currentUser: User
    ): Page<StorageObject> {
        val pageable = PageRequest.of(page, size)

        val spec = Specification<StorageObject> { root, query, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            // Role-based filtering for uploaderId
            if (currentUser.role == "ADMIN") {
                uploaderId?.let {
                    predicates.add(cb.equal(root.get<User>("uploader").get<UUID>("id"), it))
                }
            } else {
                predicates.add(cb.equal(root.get<User>("uploader").get<UUID>("id"), currentUser.id))
            }

            // Keyword filtering
            keyword?.let {
                if (it.isNotBlank()) {
                    predicates.add(cb.like(root.get("originalFilename"), "%${it.trim()}%"))
                }
            }

            query?.orderBy(cb.desc(root.get<Any>("createdAt")))

            cb.and(*predicates.toTypedArray())
        }

        return storageObjectRepository.findAll(spec, pageable)
    }
} 
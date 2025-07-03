package edu.jimei.janus.domain.storage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StorageObjectRepository : JpaRepository<StorageObject, UUID> {
    fun findByObjectKey(objectKey: String): StorageObject?
} 
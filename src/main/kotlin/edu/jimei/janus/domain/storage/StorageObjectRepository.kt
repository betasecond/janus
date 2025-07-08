package edu.jimei.janus.domain.storage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StorageObjectRepository : JpaRepository<StorageObject, UUID>, JpaSpecificationExecutor<StorageObject> {
    fun findByObjectKey(objectKey: String): StorageObject?
} 
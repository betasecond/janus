package edu.jimei.janus.controller.vo

import edu.jimei.janus.controller.mapper.StorageObjectVOMapper
import edu.jimei.janus.domain.storage.StorageObject

data class StorageObjectVO(
    val id: String,
    val objectKey: String,
    val url: String,
    val originalFilename: String,
    val contentType: String,
    val fileSize: Long,
    val uploaderId: String,
    val createdAt: String
)

/**
 * 扩展函数：将StorageObject转换为StorageObjectVO
 * 注意：这个扩展函数需要StorageObjectVOMapper实例，应该在Controller中使用mapper
 */
fun StorageObject.toVo(mapper: StorageObjectVOMapper): StorageObjectVO {
    return mapper.toVO(this)
} 
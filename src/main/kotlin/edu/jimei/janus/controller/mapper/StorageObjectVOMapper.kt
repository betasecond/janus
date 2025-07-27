package edu.jimei.janus.controller.mapper

import edu.jimei.janus.application.service.OssService
import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.controller.vo.StorageObjectVO
import edu.jimei.janus.domain.storage.StorageObject
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * 存储对象VO映射器
 * 负责将StorageObject实体映射为StorageObjectVO，确保字段名称对齐和枚举值转换
 */
@Component
class StorageObjectVOMapper(
    private val enumConverter: EnumConverter,
    private val ossService: OssService
) {
    
    /**
     * 将StorageObject实体转换为StorageObjectVO
     * @param storageObject StorageObject实体
     * @return StorageObjectVO 前端视图对象
     */
    fun toVO(storageObject: StorageObject): StorageObjectVO {
        return StorageObjectVO(
            id = storageObject.id.toString(),
            objectKey = storageObject.objectKey,
            url = generateFileUrl(storageObject.objectKey),
            originalFilename = storageObject.originalFilename,
            contentType = storageObject.contentType ?: "",
            fileSize = storageObject.fileSize,
            uploaderId = storageObject.uploader?.id?.toString() ?: "",
            createdAt = formatDateTime(storageObject.createdAt)
        )
    }
    
    /**
     * 生成文件的可访问URL
     * @param objectKey 对象存储中的文件键
     * @return 可访问的文件URL
     */
    private fun generateFileUrl(objectKey: String): String {
        return try {
            ossService.generatePresignedUrl(objectKey, 60).toString()
        } catch (e: Exception) {
            // 如果生成URL失败，返回空字符串
            ""
        }
    }
    
    /**
     * 将LocalDateTime格式化为ISO 8601字符串
     * @param dateTime LocalDateTime对象
     * @return ISO 8601格式的日期时间字符串
     */
    private fun formatDateTime(dateTime: java.time.LocalDateTime?): String {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""
    }
}
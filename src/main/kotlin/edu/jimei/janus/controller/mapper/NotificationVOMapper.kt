package edu.jimei.janus.controller.mapper

import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.controller.vo.NotificationVO
import edu.jimei.janus.domain.notification.Notification
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * 通知VO映射器
 * 负责将Notification实体映射为NotificationVO，确保字段名称对齐和枚举值转换
 */
@Component
class NotificationVOMapper(
    private val enumConverter: EnumConverter
) {
    
    companion object {
        private val ISO_8601_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
    
    /**
     * 将Notification实体转换为NotificationVO
     * @param notification Notification实体
     * @return NotificationVO 前端视图对象
     */
    fun toVO(notification: Notification): NotificationVO {
        return NotificationVO(
            id = notification.id.toString(),
            title = notification.title,
            content = notification.content,
            type = enumConverter.convertNotificationType(notification.type), // 转换为前端期望的格式
            isRead = notification.isRead,
            createdAt = notification.createdAt?.format(ISO_8601_FORMATTER) ?: "", // ISO 8601格式
            senderId = notification.sender?.id?.toString() // 转换为可选的字符串ID
        )
    }
}
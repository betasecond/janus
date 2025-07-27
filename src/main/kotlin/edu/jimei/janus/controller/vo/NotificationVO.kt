package edu.jimei.janus.controller.vo

/**
 * 通知视图对象
 * 符合前端规范的通知数据结构
 * @property id 通知ID（字符串格式）
 * @property title 通知标题
 * @property content 通知内容
 * @property type 通知类型（大写格式：INFO, WARNING, SUCCESS, ERROR）
 * @property isRead 是否已读
 * @property createdAt 创建时间（ISO 8601格式）
 * @property senderId 发送者ID（可选的字符串类型）
 */
data class NotificationVO(
    val id: String,
    val title: String,
    val content: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String,
    val senderId: String?
) 
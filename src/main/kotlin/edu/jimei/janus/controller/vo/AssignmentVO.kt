package edu.jimei.janus.controller.vo

/**
 * 作业视图对象
 * 符合前端规范的作业数据结构
 * @property id 作业ID（字符串格式）
 * @property title 作业标题
 * @property description 作业描述
 * @property dueDate 截止日期（ISO 8601格式字符串）
 * @property courseId 课程ID（字符串格式）
 * @property questionIds 题目ID数组（字符串数组）
 * @property createdAt 创建时间（ISO 8601格式字符串）
 */
data class AssignmentVO(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: String?, // ISO 8601 format
    val courseId: String,
    val questionIds: List<String>,
    val createdAt: String // ISO 8601 format
)
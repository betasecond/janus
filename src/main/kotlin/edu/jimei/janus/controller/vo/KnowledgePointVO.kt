package edu.jimei.janus.controller.vo

/**
 * 知识点视图对象，用于前端数据展示
 * 符合前端数据结构规范
 */
data class KnowledgePointVO(
    val id: String,
    val name: String,
    val description: String?,
    val subject: String?,
    val parentId: String?
)
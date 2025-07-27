package edu.jimei.janus.controller.vo

/**
 * 题目视图对象，用于前端数据展示
 * 符合前端数据结构规范
 */
data class QuestionVO(
    val id: String,
    val content: String,
    val type: String, // SINGLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY
    val difficulty: String, // EASY, MEDIUM, HARD
    val knowledgePointIds: List<String>,
    val options: Map<String, String>?, // 选择题选项，其他题型为null
    val correctAnswer: String?,
    val explanation: String?
)
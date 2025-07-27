package edu.jimei.janus.controller.vo

import java.math.BigDecimal

/**
 * 作业提交视图对象
 * 符合前端规范的作业提交数据结构
 * @property id 提交ID（字符串格式）
 * @property assignmentId 作业ID（字符串格式）
 * @property studentId 学生ID（字符串格式）
 * @property answers 提交答案列表
 * @property score 得分
 * @property status 提交状态（大写格式：SUBMITTED, GRADING, GRADED）
 * @property submittedAt 提交时间（ISO 8601格式字符串）
 */
data class AssignmentSubmissionVO(
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val answers: List<SubmissionAnswerVO>,
    val score: BigDecimal?,
    val status: String, // SUBMITTED, GRADING, GRADED
    val submittedAt: String? // ISO 8601 format
)

/**
 * 提交答案视图对象
 * 符合前端规范的提交答案数据结构
 * @property id 答案ID（字符串格式）
 * @property questionId 题目ID（字符串格式）
 * @property answer 学生答案（JSON格式字符串）
 * @property isCorrect 是否正确
 */
data class SubmissionAnswerVO(
    val id: String,
    val questionId: String,
    val answer: String?, // JSON format
    val isCorrect: Boolean?
)
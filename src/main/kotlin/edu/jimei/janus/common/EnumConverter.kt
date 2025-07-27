package edu.jimei.janus.common

import edu.jimei.janus.domain.question.QuestionType
import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.assignment.SubmissionStatus
import edu.jimei.janus.domain.notification.NotificationType
import edu.jimei.janus.domain.storage.EmbeddingStatus
import org.springframework.stereotype.Component

/**
 * 枚举转换器
 * 处理数据库枚举值到前端枚举值的转换，确保所有枚举值都转换为大写格式
 */
@Component
class EnumConverter {
    
    /**
     * 转换用户角色枚举值
     * 将数据库中的角色值转换为前端期望的大写格式
     * @param role 数据库中的角色值（如 "teacher", "student", "admin"）
     * @return 大写格式的角色值（如 "TEACHER", "STUDENT", "ADMIN"）
     */
    fun convertUserRole(role: String): String {
        return role.uppercase()
    }
    
    /**
     * 转换题目类型枚举值
     * 将数据库中的题目类型转换为前端期望的格式
     * @param type 数据库中的题目类型枚举
     * @return 前端期望的题目类型字符串
     */
    fun convertQuestionType(type: QuestionType): String {
        return when (type) {
            QuestionType.MULTIPLE_CHOICE -> "SINGLE_CHOICE"  // 数据库中的MULTIPLE_CHOICE对应前端的SINGLE_CHOICE
            QuestionType.TRUE_FALSE -> "TRUE_FALSE"
            QuestionType.SHORT_ANSWER -> "SHORT_ANSWER"
            QuestionType.ESSAY -> "ESSAY"
        }
    }
    
    /**
     * 转换难度等级枚举值
     * 将数据库中的难度等级转换为前端期望的大写格式
     * @param difficulty 数据库中的难度等级枚举
     * @return 大写格式的难度等级字符串
     */
    fun convertDifficulty(difficulty: Difficulty): String {
        return difficulty.name // 已经是大写格式：EASY, MEDIUM, HARD
    }
    
    /**
     * 转换作业提交状态枚举值
     * 将数据库中的提交状态转换为前端期望的大写格式
     * @param status 数据库中的提交状态枚举
     * @return 大写格式的提交状态字符串
     */
    fun convertSubmissionStatus(status: SubmissionStatus): String {
        return status.name // 已经是大写格式：SUBMITTED, GRADING, GRADED
    }
    
    /**
     * 转换通知类型枚举值
     * 将数据库中的通知类型转换为前端期望的格式
     * @param type 数据库中的通知类型枚举
     * @return 前端期望的通知类型字符串
     */
    fun convertNotificationType(type: NotificationType): String {
        return when (type) {
            NotificationType.ASSIGNMENT -> "INFO"
            NotificationType.GRADE -> "SUCCESS"
            NotificationType.SYSTEM -> "WARNING"
            NotificationType.COURSE -> "INFO"
        }
    }
    
    /**
     * 转换嵌入状态枚举值
     * 将数据库中的嵌入状态转换为前端期望的大写格式
     * @param status 数据库中的嵌入状态枚举
     * @return 大写格式的嵌入状态字符串
     */
    fun convertEmbeddingStatus(status: EmbeddingStatus): String {
        return status.name // 已经是大写格式：PENDING, COMPLETED, FAILED
    }
    
    /**
     * 反向转换：将前端的用户角色转换为数据库格式
     * @param role 前端的大写角色值
     * @return 数据库中的小写角色值
     */
    fun convertUserRoleFromFrontend(role: String): String {
        return role.lowercase()
    }
    
    /**
     * 反向转换：将前端的题目类型转换为数据库枚举
     * @param type 前端的题目类型字符串
     * @return 数据库中的题目类型枚举
     */
    fun convertQuestionTypeFromFrontend(type: String): QuestionType {
        return when (type.uppercase()) {
            "SINGLE_CHOICE" -> QuestionType.MULTIPLE_CHOICE
            "TRUE_FALSE" -> QuestionType.TRUE_FALSE
            "SHORT_ANSWER" -> QuestionType.SHORT_ANSWER
            "ESSAY" -> QuestionType.ESSAY
            else -> throw IllegalArgumentException("Unknown question type: $type")
        }
    }
    
    /**
     * 反向转换：将前端的难度等级转换为数据库枚举
     * @param difficulty 前端的难度等级字符串
     * @return 数据库中的难度等级枚举
     */
    fun convertDifficultyFromFrontend(difficulty: String): Difficulty {
        return when (difficulty.uppercase()) {
            "EASY" -> Difficulty.EASY
            "MEDIUM" -> Difficulty.MEDIUM
            "HARD" -> Difficulty.HARD
            else -> throw IllegalArgumentException("Unknown difficulty: $difficulty")
        }
    }
    
    /**
     * 反向转换：将前端的提交状态转换为数据库枚举
     * @param status 前端的提交状态字符串
     * @return 数据库中的提交状态枚举
     */
    fun convertSubmissionStatusFromFrontend(status: String): SubmissionStatus {
        return when (status.uppercase()) {
            "SUBMITTED" -> SubmissionStatus.SUBMITTED
            "GRADING" -> SubmissionStatus.GRADING
            "GRADED" -> SubmissionStatus.GRADED
            else -> throw IllegalArgumentException("Unknown submission status: $status")
        }
    }
    
    /**
     * 反向转换：将前端的通知类型转换为数据库枚举
     * @param type 前端的通知类型字符串
     * @return 数据库中的通知类型枚举
     */
    fun convertNotificationTypeFromFrontend(type: String): NotificationType {
        return when (type.uppercase()) {
            "INFO" -> NotificationType.ASSIGNMENT // 默认映射到ASSIGNMENT
            "SUCCESS" -> NotificationType.GRADE
            "WARNING" -> NotificationType.SYSTEM
            "ERROR" -> NotificationType.SYSTEM
            else -> throw IllegalArgumentException("Unknown notification type: $type")
        }
    }
    
    /**
     * 反向转换：将前端的嵌入状态转换为数据库枚举
     * @param status 前端的嵌入状态字符串
     * @return 数据库中的嵌入状态枚举
     */
    fun convertEmbeddingStatusFromFrontend(status: String): EmbeddingStatus {
        return when (status.uppercase()) {
            "PENDING" -> EmbeddingStatus.PENDING
            "COMPLETED" -> EmbeddingStatus.COMPLETED
            "FAILED" -> EmbeddingStatus.FAILED
            else -> throw IllegalArgumentException("Unknown embedding status: $status")
        }
    }
}
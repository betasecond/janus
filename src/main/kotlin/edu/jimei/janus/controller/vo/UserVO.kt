package edu.jimei.janus.controller.vo

/**
 * 用户视图对象
 * 符合前端规范的用户数据结构
 * @property id 用户ID（字符串格式）
 * @property displayName 显示名称
 * @property email 邮箱地址
 * @property avatarUrl 头像URL
 * @property role 用户角色（大写格式：TEACHER, STUDENT, ADMIN）
 */
data class UserVO(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String,
    val role: String
) 
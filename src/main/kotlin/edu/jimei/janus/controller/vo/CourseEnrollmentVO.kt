package edu.jimei.janus.controller.vo

import java.time.LocalDateTime

/**
 * 课程选课视图对象
 * 用于独立的选课API端点，移除Course中的students数量字段
 * @property id 选课记录ID（字符串格式）
 * @property courseId 课程ID（字符串格式）
 * @property student 学生信息（完整UserVO对象）
 * @property enrolledAt 选课时间
 */
data class CourseEnrollmentVO(
    val id: String,
    val courseId: String,
    val student: UserVO,
    val enrolledAt: String // ISO 8601格式的时间字符串
)

/**
 * 课程选课统计视图对象
 * 用于获取课程的选课统计信息
 * @property courseId 课程ID（字符串格式）
 * @property totalStudents 总学生数
 * @property enrollments 选课记录列表
 */
data class CourseEnrollmentStatsVO(
    val courseId: String,
    val totalStudents: Long,
    val enrollments: List<CourseEnrollmentVO>
)
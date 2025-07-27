package edu.jimei.janus.controller.vo

/**
 * 课程视图对象
 * 符合前端规范的课程数据结构
 * @property id 课程ID（字符串格式）
 * @property name 课程名称
 * @property description 课程描述
 * @property teacher 授课教师（完整UserVO对象）
 * @property coverImageUrl 课程封面图片URL
 */
data class CourseVO(
    val id: String,
    val name: String,
    val description: String,
    val teacher: UserVO,
    val coverImageUrl: String?
)
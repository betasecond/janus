package edu.jimei.janus.controller.mapper

import edu.jimei.janus.controller.vo.CourseVO
import edu.jimei.janus.domain.course.Course
import org.springframework.stereotype.Component

/**
 * 课程VO映射器
 * 负责将Course实体映射为CourseVO，确保字段名称对齐和teacher对象转换
 */
@Component
class CourseVOMapper(
    private val userVOMapper: UserVOMapper
) {
    
    /**
     * 将Course实体转换为CourseVO
     * @param course Course实体
     * @return CourseVO 前端视图对象
     */
    fun toVO(course: Course): CourseVO {
        return CourseVO(
            id = course.id.toString(),
            name = course.name,
            description = course.description ?: "", // 如果description为null，返回空字符串
            teacher = userVOMapper.toVO(course.teacher), // 通过UserVOMapper转换为完整的UserVO对象
            coverImageUrl = course.coverImageUrl // 保持coverImageUrl字段对齐
        )
    }
}
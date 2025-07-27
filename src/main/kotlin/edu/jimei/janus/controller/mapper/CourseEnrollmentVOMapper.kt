package edu.jimei.janus.controller.mapper

import edu.jimei.janus.controller.vo.CourseEnrollmentVO
import edu.jimei.janus.controller.vo.CourseEnrollmentStatsVO
import edu.jimei.janus.domain.course.Course
import edu.jimei.janus.domain.user.User
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * 课程选课VO映射器
 * 负责将Course-Student关系映射为CourseEnrollmentVO
 */
@Component
class CourseEnrollmentVOMapper(
    private val userVOMapper: UserVOMapper
) {
    
    /**
     * 将课程和学生信息转换为CourseEnrollmentVO
     * @param course 课程实体
     * @param student 学生实体
     * @return CourseEnrollmentVO 选课视图对象
     */
    fun toVO(course: Course, student: User): CourseEnrollmentVO {
        return CourseEnrollmentVO(
            id = "${course.id}-${student.id}", // 生成复合ID
            courseId = course.id.toString(),
            student = userVOMapper.toVO(student),
            enrolledAt = course.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""
        )
    }
    
    /**
     * 将课程转换为CourseEnrollmentStatsVO
     * @param course 课程实体
     * @return CourseEnrollmentStatsVO 选课统计视图对象
     */
    fun toStatsVO(course: Course): CourseEnrollmentStatsVO {
        val enrollments = course.students.map { student ->
            toVO(course, student)
        }
        
        return CourseEnrollmentStatsVO(
            courseId = course.id.toString(),
            totalStudents = course.students.size.toLong(),
            enrollments = enrollments
        )
    }
}
package edu.jimei.janus.controller.mapper

import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.controller.vo.AssignmentVO
import edu.jimei.janus.controller.vo.AssignmentSubmissionVO
import edu.jimei.janus.controller.vo.SubmissionAnswerVO
import edu.jimei.janus.domain.assignment.Assignment
import edu.jimei.janus.domain.assignment.AssignmentSubmission
import edu.jimei.janus.domain.assignment.SubmissionAnswer
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * 作业VO映射器
 * 负责将Assignment实体映射为AssignmentVO，确保字段名称对齐和枚举值转换
 */
@Component
class AssignmentVOMapper(
    private val enumConverter: EnumConverter
) {
    
    companion object {
        private val ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
    
    /**
     * 将Assignment实体转换为AssignmentVO
     * @param assignment Assignment实体
     * @return AssignmentVO 前端视图对象
     */
    fun toVO(assignment: Assignment): AssignmentVO {
        return AssignmentVO(
            id = assignment.id.toString(),
            title = assignment.title,
            description = assignment.description ?: "",
            dueDate = assignment.dueDate?.format(ISO_FORMATTER), // 转换为ISO 8601格式
            courseId = assignment.course.id.toString(), // 转换course关联为courseId字符串
            questionIds = assignment.questions.map { it.id.toString() }, // 转换questions关联为questionIds数组
            createdAt = assignment.createdAt?.format(ISO_FORMATTER) ?: "" // 转换为ISO 8601格式
        )
    }
    
    /**
     * 将AssignmentSubmission实体转换为AssignmentSubmissionVO
     * @param submission AssignmentSubmission实体
     * @return AssignmentSubmissionVO 前端视图对象
     */
    fun toVO(submission: AssignmentSubmission): AssignmentSubmissionVO {
        return AssignmentSubmissionVO(
            id = submission.id.toString(),
            assignmentId = submission.assignment.id.toString(),
            studentId = submission.student.id.toString(),
            answers = submission.answers.map { toVO(it) },
            score = submission.score,
            status = enumConverter.convertSubmissionStatus(submission.status), // 转换为大写格式
            submittedAt = submission.submittedAt?.format(ISO_FORMATTER) // 转换为ISO 8601格式
        )
    }
    
    /**
     * 将SubmissionAnswer实体转换为SubmissionAnswerVO
     * @param answer SubmissionAnswer实体
     * @return SubmissionAnswerVO 前端视图对象
     */
    fun toVO(answer: SubmissionAnswer): SubmissionAnswerVO {
        return SubmissionAnswerVO(
            id = answer.id.toString(),
            questionId = answer.question.id.toString(),
            answer = answer.answer, // JSON格式字符串，保持原样
            isCorrect = answer.isCorrect
        )
    }
}
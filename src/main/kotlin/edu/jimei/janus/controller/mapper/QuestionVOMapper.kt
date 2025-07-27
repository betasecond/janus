package edu.jimei.janus.controller.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.controller.vo.QuestionVO
import edu.jimei.janus.domain.question.Question
import edu.jimei.janus.domain.question.QuestionType
import org.springframework.stereotype.Component

/**
 * 题目VO映射器
 * 负责将Question实体映射为QuestionVO，处理复杂的content字段解析和枚举值转换
 */
@Component
class QuestionVOMapper(
    private val enumConverter: EnumConverter,
    private val objectMapper: ObjectMapper
){

    /**
     * 将Question实体转换为QuestionVO
     * @param question Question实体
     * @return QuestionVO 前端视图对象
     */
    fun toVO(question: Question): QuestionVO {
        return QuestionVO(
            id = question.id.toString(),
            content = parseQuestionContent(question.content),
            type = enumConverter.convertQuestionType(question.type),
            difficulty = enumConverter.convertDifficulty(question.difficulty),
            knowledgePointIds = question.knowledgePoints.map { it.id.toString() },
            options = parseQuestionOptions(question.content, question.type),
            correctAnswer = question.correctAnswer,
            explanation = question.explanation
        )
    }

    /**
     * 解析JSON格式的content字段，提取题干内容
     * @param content JSON格式的题目内容
     * @return 题干文本
     */
    private fun parseQuestionContent(content: String): String {
        return try {
            val contentJson: JsonNode = objectMapper.readTree(content)

            // 尝试获取题干内容，支持多种可能的字段名
            when {
                contentJson.has("question") -> contentJson.get("question").asText()
                contentJson.has("stem") -> contentJson.get("stem").asText()
                contentJson.has("content") -> contentJson.get("content").asText()
                contentJson.has("text") -> contentJson.get("text").asText()
                contentJson.isTextual -> contentJson.asText()
                else -> content // 如果无法解析，返回原始内容
            }
        } catch (e: Exception) {
            // 如果JSON解析失败，返回原始内容
            content
        }
    }

    /**
     * 解析JSON格式的content字段，提取选项内容
     * @param content JSON格式的题目内容
     * @param type 题目类型
     * @return 选项映射，非选择题返回null
     */
    private fun parseQuestionOptions(content: String, type: QuestionType): Map<String, String>? {
        // 只有选择题才有选项
        if (type != QuestionType.MULTIPLE_CHOICE && type != QuestionType.TRUE_FALSE) {
            return null
        }

        return try {
            val contentJson: JsonNode = objectMapper.readTree(content)

            // 尝试获取选项内容，支持多种可能的字段名
            val optionsNode = when {
                contentJson.has("options") -> contentJson.get("options")
                contentJson.has("choices") -> contentJson.get("choices")
                contentJson.has("answers") -> contentJson.get("answers")
                else -> null
            }

            optionsNode?.let { node ->
                when {
                    // 如果选项是对象格式 {"A": "选项A", "B": "选项B"}
                    node.isObject -> {
                        val options = mutableMapOf<String, String>()
                        node.fields().forEach { (key, value) ->
                            options[key] = value.asText()
                        }
                        options
                    }
                    // 如果选项是数组格式 ["选项A", "选项B", "选项C", "选项D"]
                    node.isArray -> {
                        val options = mutableMapOf<String, String>()
                        val labels = listOf("A", "B", "C", "D", "E", "F")
                        node.forEachIndexed { index, value ->
                            if (index < labels.size) {
                                options[labels[index]] = value.asText()
                            }
                        }
                        options
                    }
                    else -> null
                }
            }
        } catch (e: Exception) {
            // 如果JSON解析失败，返回null
            null
        }
    }
}

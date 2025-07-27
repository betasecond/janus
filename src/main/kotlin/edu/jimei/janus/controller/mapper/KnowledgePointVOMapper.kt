package edu.jimei.janus.controller.mapper

import edu.jimei.janus.controller.vo.KnowledgePointVO
import edu.jimei.janus.domain.knowledge.KnowledgePoint
import org.springframework.stereotype.Component

/**
 * 知识点VO映射器
 * 负责将KnowledgePoint实体映射为KnowledgePointVO
 */
@Component
class KnowledgePointVOMapper {
    
    /**
     * 将KnowledgePoint实体转换为KnowledgePointVO
     * @param knowledgePoint KnowledgePoint实体
     * @return KnowledgePointVO 前端视图对象
     */
    fun toVO(knowledgePoint: KnowledgePoint): KnowledgePointVO {
        return KnowledgePointVO(
            id = knowledgePoint.id.toString(),
            name = knowledgePoint.name,
            description = knowledgePoint.description,
            subject = knowledgePoint.subject,
            parentId = knowledgePoint.parent?.id?.toString()
        )
    }
}
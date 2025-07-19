package edu.jimei.janus.controller.dto

import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.QuestionType
import java.util.UUID

data class QuestionQuery(
    val type: QuestionType? = null,
    val difficulty: Difficulty? = null,
    val creatorId: UUID? = null,
    val knowledgePointId: UUID? = null,
    val subject: String? = null
) 
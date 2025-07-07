package edu.jimei.janus.controller.vo

import edu.jimei.janus.domain.question.Difficulty
import edu.jimei.janus.domain.question.QuestionType

data class QuestionStatsVO(
    val total: Int,
    val byType: Map<QuestionType, Int>,
    val byDifficulty: Map<Difficulty, Int>
) 
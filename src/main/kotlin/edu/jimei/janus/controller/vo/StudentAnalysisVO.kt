package edu.jimei.janus.controller.vo

import java.util.UUID

data class StudentAnalysisVO(
    val id: UUID,
    val studentId: UUID,
    val studentName: String,
    val incorrectQuestions: String,
    val errorLocation: String, // This might be simplified
    val suggestedCorrection: String // This will likely require AI service
) 
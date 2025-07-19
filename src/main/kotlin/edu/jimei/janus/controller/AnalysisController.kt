package edu.jimei.janus.controller

import edu.jimei.janus.controller.vo.StudentAnalysisVO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/analysis")
class AnalysisController {

    /**
     * Retrieves student analysis data.
     *
     * NOTE: This is a mocked implementation. Generating a real student analysis
     * (especially the 'suggestedCorrection' part) would require a more complex
     * service layer, potentially involving AI to analyze incorrect answers.
     *
     * This implementation returns a hardcoded list of analysis results
     * to match the frontend's mock data structure, allowing the frontend
     * to integrate with a working endpoint.
     */
    @GetMapping
    fun getStudentAnalysis(@RequestParam(required = false) userId: UUID?): ResponseEntity<List<StudentAnalysisVO>> {
        // Mocked data, similar to the frontend's 'mockStudentAnalysis'
        val mockAnalysis = listOf(
            StudentAnalysisVO(
                id = UUID.randomUUID(),
                studentId = UUID.randomUUID(),
                studentName = "Ethan Harper (Mock)",
                incorrectQuestions = "Question 5, Question 8",
                errorLocation = "Step 2, Step 4",
                suggestedCorrection = "Review algebraic equations"
            ),
            StudentAnalysisVO(
                id = UUID.randomUUID(),
                studentId = UUID.randomUUID(),
                studentName = "Olivia Bennett (Mock)",
                incorrectQuestions = "Question 3, Question 7",
                errorLocation = "Step 1, Step 3",
                suggestedCorrection = "Practice geometry proofs"
            ),
            StudentAnalysisVO(
                id = UUID.randomUUID(),
                studentId = UUID.randomUUID(),
                studentName = "Noah Carter (Mock)",
                incorrectQuestions = "Question 2, Question 6",
                errorLocation = "Step 3, Step 5",
                suggestedCorrection = "Understand function concepts"
            )
        )
        return ResponseEntity.ok(mockAnalysis)
    }
} 
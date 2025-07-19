package edu.jimei.janus.controller

import edu.jimei.janus.controller.vo.SyllabusVO
import edu.jimei.janus.controller.vo.toSyllabusVo
import edu.jimei.janus.domain.lesson.LessonPlanRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/syllabus")
class SyllabusController(private val lessonPlanRepository: LessonPlanRepository) {

    /**
     * Retrieves the syllabus.
     *
     * NOTE: This is a simplified implementation. The frontend mock API does not specify
     * which syllabus to fetch. This implementation fetches the *first* available
     * lesson plan from the database. In a real-world scenario, you would likely
     * need to pass a course ID or lesson plan ID.
     */
    @GetMapping
    fun getSyllabus(): ResponseEntity<SyllabusVO> {
        // Find the first lesson plan available.
        val lessonPlan = lessonPlanRepository.findAll().firstOrNull()

        return if (lessonPlan != null) {
            ResponseEntity.ok(lessonPlan.toSyllabusVo())
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 
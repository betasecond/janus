# Janus 智能教辅系统后端 TODO 清单

## 概述
根据PRD文档和README接口规范，当前后端系统缺失大量核心功能模块。本文档详细分析了已实现功能与缺失功能，并提供了完整的开发roadmap。

## 📊 当前状态分析

### ✅ 已完成的基础设施
- **数据库Schema**: 完整的表结构设计 (V1__initial_schema.sql) 包含用户、课程、题目、作业等所有核心表
- **文件存储系统**: StorageController + OssService (阿里云OSS集成)
- **AI嵌入向量**: EmbeddingController + EmbeddingService (Spring AI集成)
- **用户实体**: User domain model + UserRepository (完整实现)
- **存储实体**: StorageObject domain model + StorageObjectRepository (完整实现)
- **文件处理**: FileProcessService, DataIngestionService (PDF/Markdown处理)
- **基础配置**: OSS配置、Redis向量存储、Pulsar消息队列、Flyway数据库迁移

### ❌ 主要缺失: 业务层实现 (数据库Schema已设计好)

## 🎯 一、Controller层缺失 (优先级: 高)

### 1.1 用户管理模块
**接口**: `/api/users`
```kotlin
// 需要实现的接口
@RestController
@RequestMapping("/api/users")
class UserController {
    // GET /api/users - 获取用户列表
    // POST /api/users - 创建新用户  
    // PUT /api/users/{id} - 更新用户信息
    // DELETE /api/users/{id} - 删除用户
    // GET /api/users/{id} - 获取用户详情
    // GET /api/users/search - 用户搜索
}
```

### 1.2 课程管理模块  
**接口**: `/api/courses`
```kotlin
@RestController
@RequestMapping("/api/courses")
class CourseController {
    // GET /api/courses - 获取课程列表
    // POST /api/courses - 创建课程
    // PUT /api/courses/{id} - 更新课程
    // DELETE /api/courses/{id} - 删除课程
    // GET /api/courses/{id}/students - 获取课程学生
    // POST /api/courses/{id}/enroll - 学生选课
}
```

### 1.3 题目管理模块
**接口**: `/api/questions`  
```kotlin
@RestController
@RequestMapping("/api/questions")
class QuestionController {
    // GET /api/questions - 获取题目列表
    // POST /api/questions - 创建题目
    // PUT /api/questions/{id} - 更新题目
    // DELETE /api/questions/{id} - 删除题目
    // GET /api/questions/search - 按知识点/难度搜索
    // POST /api/questions/batch - 批量导入题目
}
```

### 1.4 作业管理模块
**接口**: `/api/assignments`
```kotlin  
@RestController
@RequestMapping("/api/assignments")
class AssignmentController {
    // GET /api/assignments - 获取作业列表
    // POST /api/assignments - 创建作业
    // PUT /api/assignments/{id} - 更新作业
    // DELETE /api/assignments/{id} - 删除作业
    // POST /api/assignments/{id}/submit - 提交作业
    // GET /api/assignments/{id}/submissions - 获取提交记录
    // POST /api/assignments/{id}/grade - 批改作业
}
```

### 1.5 通知管理模块
**接口**: `/api/notifications`
```kotlin
@RestController  
@RequestMapping("/api/notifications")
class NotificationController {
    // GET /api/notifications - 获取通知列表
    // POST /api/notifications - 发送通知
    // PUT /api/notifications/{id}/read - 标记已读
    // DELETE /api/notifications/{id} - 删除通知
    // GET /api/notifications/unread - 获取未读通知
}
```

### 1.6 统计分析模块
**接口**: `/api/stats`, `/api/analysis`
```kotlin
@RestController
@RequestMapping("/api/stats")
class StatsController {
    // GET /api/stats/performance - 性能统计
    // GET /api/stats/users - 用户统计  
    // GET /api/stats/resources - 资源统计
    // GET /api/stats/system - 系统状态
}

@RestController
@RequestMapping("/api/analysis") 
class AnalysisController {
    // GET /api/analysis/students/{id} - 学生学情分析
    // GET /api/analysis/class/{id} - 班级分析
    // GET /api/analysis/knowledge-points - 知识点掌握分析
    // POST /api/analysis/generate-report - 生成分析报告
}
```

### 1.7 教学大纲模块
**接口**: `/api/syllabus`
```kotlin
@RestController
@RequestMapping("/api/syllabus")
class SyllabusController {
    // GET /api/syllabus - 获取大纲列表
    // POST /api/syllabus - 创建大纲
    // PUT /api/syllabus/{id} - 更新大纲  
    // DELETE /api/syllabus/{id} - 删除大纲
    // POST /api/syllabus/{id}/generate - AI生成教学内容
    // GET /api/syllabus/{id}/status - 获取生成状态
}
```

### 1.8 菜单导航模块
**接口**: `/api/menu/{role}`
```kotlin
@RestController
@RequestMapping("/api/menu")
class MenuController {
    // GET /api/menu/{role} - 获取角色菜单
    // POST /api/menu - 创建菜单项
    // PUT /api/menu/{id} - 更新菜单
    // DELETE /api/menu/{id} - 删除菜单
}
```

### 1.9 资源管理模块  
**接口**: `/api/resources`
```kotlin
@RestController
@RequestMapping("/api/resources") 
class ResourceController {
    // GET /api/resources - 获取资源列表
    // POST /api/resources - 上传资源
    // PUT /api/resources/{id} - 更新资源信息
    // DELETE /api/resources/{id} - 删除资源
    // GET /api/resources/search - 资源搜索
    // GET /api/resources/{id}/download - 下载资源
}
```

### 1.10 认证授权模块
**接口**: `/api/auth`
```kotlin
@RestController
@RequestMapping("/api/auth")
class AuthController {
    // POST /api/auth/login - 用户登录
    // POST /api/auth/logout - 用户登出
    // POST /api/auth/refresh - 刷新Token
    // GET /api/auth/profile - 获取用户信息
    // POST /api/auth/change-password - 修改密码
}
```

## 🗄️ 二、Domain实体模型缺失 (优先级: 高)
> **注意**: 数据库表结构已在 V1__initial_schema.sql 中完整设计，需要创建对应的Kotlin实体类

### 2.1 课程实体
```kotlin
@Entity
@Table(name = "janus_courses")
class Course(
    @Id @GeneratedValue val id: UUID? = null,
    var name: String,
    var description: String?,
    var subject: String,
    var grade: String?,
    @ManyToOne var teacher: User,
    @ManyToMany var students: MutableSet<User> = mutableSetOf(),
    @CreatedDate var createdAt: LocalDateTime? = null,
    @LastModifiedDate var updatedAt: LocalDateTime? = null
)
```

### 2.2 题目实体
```kotlin
@Entity
@Table(name = "janus_questions")
class Question(
    @Id @GeneratedValue val id: UUID? = null,
    var title: String,
    @Lob var content: String,
    @Enumerated(EnumType.STRING) var type: QuestionType,
    @Enumerated(EnumType.STRING) var difficulty: Difficulty,
    @ElementCollection var knowledgePoints: MutableSet<String> = mutableSetOf(),
    @Lob var answer: String?,
    @Lob var explanation: String?,
    @ManyToOne var course: Course?,
    @ManyToOne var creator: User,
    @CreatedDate var createdAt: LocalDateTime? = null
)

enum class QuestionType { MULTIPLE_CHOICE, FILL_BLANK, ESSAY, TRUE_FALSE }
enum class Difficulty { EASY, MEDIUM, HARD }
```

### 2.3 作业实体
```kotlin
@Entity
@Table(name = "janus_assignments")
class Assignment(
    @Id @GeneratedValue val id: UUID? = null,
    var title: String,
    @Lob var description: String?,
    @ManyToOne var course: Course,
    @ManyToOne var teacher: User,
    @ManyToMany var questions: MutableSet<Question> = mutableSetOf(),
    var dueDate: LocalDateTime?,
    var totalScore: Int = 0,
    @Enumerated(EnumType.STRING) var status: AssignmentStatus = AssignmentStatus.DRAFT,
    @CreatedDate var createdAt: LocalDateTime? = null
)

enum class AssignmentStatus { DRAFT, PUBLISHED, CLOSED }
```

### 2.4 作业提交实体
```kotlin
@Entity  
@Table(name = "janus_assignment_submissions")
class AssignmentSubmission(
    @Id @GeneratedValue val id: UUID? = null,
    @ManyToOne var assignment: Assignment,
    @ManyToOne var student: User,
    @OneToMany(cascade = [CascadeType.ALL]) var answers: MutableList<Answer> = mutableListOf(),
    var score: Int? = null,
    var submittedAt: LocalDateTime? = null,
    var gradedAt: LocalDateTime? = null,
    @ManyToOne var gradedBy: User? = null,
    @Lob var feedback: String?
)

@Entity
@Table(name = "janus_answers")  
class Answer(
    @Id @GeneratedValue val id: UUID? = null,
    @ManyToOne var question: Question,
    @Lob var content: String,
    var score: Int? = null,
    @Lob var feedback: String?
)
```

### 2.5 通知实体
```kotlin
@Entity
@Table(name = "janus_notifications")
class Notification(
    @Id @GeneratedValue val id: UUID? = null,
    var title: String,
    @Lob var content: String,
    @Enumerated(EnumType.STRING) var type: NotificationType,
    @ManyToOne var sender: User?,
    @ManyToOne var recipient: User,
    var isRead: Boolean = false,
    var readAt: LocalDateTime? = null,
    @CreatedDate var createdAt: LocalDateTime? = null
)

enum class NotificationType { ASSIGNMENT, GRADE, SYSTEM, COURSE }
```

### 2.6 教学大纲实体
```kotlin
@Entity
@Table(name = "janus_syllabus") 
class Syllabus(
    @Id @GeneratedValue val id: UUID? = null,
    @ManyToOne var course: Course,
    var title: String,
    @Lob var originalContent: String?,
    @OneToMany(cascade = [CascadeType.ALL]) var chapters: MutableList<Chapter> = mutableListOf(),
    @Enumerated(EnumType.STRING) var generationStatus: GenerationStatus = GenerationStatus.PENDING,
    @ManyToOne var creator: User,
    @CreatedDate var createdAt: LocalDateTime? = null
)

@Entity
@Table(name = "janus_chapters")
class Chapter(
    @Id @GeneratedValue val id: UUID? = null,
    var title: String,
    @Lob var content: String?,
    var orderIndex: Int,
    @OneToMany(cascade = [CascadeType.ALL]) var lessons: MutableList<Lesson> = mutableListOf()
)

@Entity  
@Table(name = "janus_lessons")
class Lesson(
    @Id @GeneratedValue val id: UUID? = null,
    var title: String,
    @Lob var content: String?,
    var duration: Int?, // 建议课时(分钟)
    var orderIndex: Int
)

enum class GenerationStatus { PENDING, GENERATING, COMPLETED, FAILED }
```

### 2.7 菜单实体
```kotlin
@Entity
@Table(name = "janus_menu_items")
class MenuItem(
    @Id @GeneratedValue val id: UUID? = null,
    var label: String,
    var icon: String?,
    var path: String,
    var role: String,
    var parentId: UUID? = null,
    var orderIndex: Int = 0,
    var isActive: Boolean = true
)
```

## 🔧 三、服务层缺失 (优先级: 中)

### 3.1 用户服务
```kotlin
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(userDto: CreateUserDto): User
    fun updateUser(id: UUID, userDto: UpdateUserDto): User  
    fun deleteUser(id: UUID)
    fun findUsersByRole(role: String): List<User>
    fun searchUsers(keyword: String): List<User>
    fun changePassword(id: UUID, oldPassword: String, newPassword: String)
}
```

### 3.2 课程服务
```kotlin
@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) {
    fun createCourse(courseDto: CreateCourseDto): Course
    fun enrollStudent(courseId: UUID, studentId: UUID)
    fun getCoursesForTeacher(teacherId: UUID): List<Course>
    fun getCoursesForStudent(studentId: UUID): List<Course>
    fun getCourseStatistics(courseId: UUID): CourseStats
}
```

### 3.3 题目服务
```kotlin
@Service  
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val courseRepository: CourseRepository
) {
    fun createQuestion(questionDto: CreateQuestionDto): Question
    fun searchQuestions(criteria: QuestionSearchCriteria): List<Question>
    fun importQuestionsFromFile(file: MultipartFile, courseId: UUID): List<Question>
    fun generateQuestionsWithAI(topic: String, count: Int, difficulty: Difficulty): List<Question>
}
```

### 3.4 作业服务
```kotlin
@Service
class AssignmentService(
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: AssignmentSubmissionRepository,
    private val questionRepository: QuestionRepository
) {
    fun createAssignment(assignmentDto: CreateAssignmentDto): Assignment
    fun publishAssignment(assignmentId: UUID): Assignment
    fun submitAssignment(submissionDto: SubmitAssignmentDto): AssignmentSubmission
    fun gradeAssignment(submissionId: UUID, gradeDto: GradeDto): AssignmentSubmission
    fun getAssignmentStatistics(assignmentId: UUID): AssignmentStats
    fun autoGradeObjectiveQuestions(submissionId: UUID)
}
```

### 3.5 通知服务
```kotlin
@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    fun sendNotification(notificationDto: SendNotificationDto): Notification
    fun broadcastToRole(message: String, role: String, type: NotificationType)
    fun markAsRead(notificationId: UUID, userId: UUID)
    fun getUnreadCount(userId: UUID): Long
    fun deleteOldNotifications(days: Int)
}
```

### 3.6 学情分析服务
```kotlin
@Service
class AnalysisService(
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: AssignmentSubmissionRepository,
    private val questionRepository: QuestionRepository
) {
    fun analyzeStudentPerformance(studentId: UUID): StudentAnalysis
    fun analyzeClassPerformance(courseId: UUID): ClassAnalysis  
    fun analyzeKnowledgePointMastery(courseId: UUID): List<KnowledgePointAnalysis>
    fun generatePerformanceReport(studentId: UUID, period: DateRange): PerformanceReport
    fun identifyWeakKnowledgePoints(studentId: UUID): List<String>
    fun getFrequentlyMissedQuestions(courseId: UUID): List<Question>
}
```

### 3.7 大纲服务
```kotlin
@Service
class SyllabusService(
    private val syllabusRepository: SyllabusRepository,
    private val aiService: AIContentGenerationService,
    private val fileProcessingService: FileProcessService
) {
    fun createSyllabus(syllabusDto: CreateSyllabusDto): Syllabus
    fun uploadAndParseSyllabus(file: MultipartFile, courseId: UUID): Syllabus
    fun generateContentWithAI(syllabusId: UUID): GenerationTask
    fun getGenerationStatus(syllabusId: UUID): GenerationStatus
    fun exportSyllabusAsPdf(syllabusId: UUID): ByteArray
}
```

## 🔐 四、认证授权功能 (优先级: 高)

### 4.1 Spring Security配置
```kotlin
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain
    
    @Bean  
    fun passwordEncoder(): PasswordEncoder
    
    @Bean
    fun jwtDecoder(): JwtDecoder
    
    @Bean
    fun jwtEncoder(): JwtEncoder
}
```

### 4.2 JWT Token服务
```kotlin
@Service
class JwtTokenService(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
) {
    fun generateToken(user: User): String
    fun validateToken(token: String): Boolean  
    fun getUserFromToken(token: String): User
    fun refreshToken(refreshToken: String): String
}
```

### 4.3 认证服务
```kotlin
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService
) {
    fun authenticate(username: String, password: String): AuthResponse
    fun refreshToken(refreshToken: String): AuthResponse
    fun logout(token: String)
    fun getCurrentUser(): User
}
```

## 🤖 五、AI智能功能增强 (优先级: 中)

### 5.1 AI内容生成服务
```kotlin
@Service
class AIContentGenerationService(
    private val chatModel: ChatModel,
    private val embeddingModel: EmbeddingModel,
    private val vectorStore: VectorStore
) {
    fun generateLessonContent(syllabus: String, chapter: String): LessonContent
    fun generateQuestions(topic: String, difficulty: Difficulty, count: Int): List<Question>
    fun generateExplanation(question: Question, studentAnswer: String): String
    fun recommendStudyMaterials(studentId: UUID, weakKnowledgePoints: List<String>): List<Resource>
    fun generateAssignmentFeedback(submission: AssignmentSubmission): String
}
```

### 5.2 智能组卷服务
```kotlin
@Service  
class IntelligentPaperGenerationService(
    private val questionRepository: QuestionRepository,
    private val analysisService: AnalysisService
) {
    fun generatePaper(criteria: PaperGenerationCriteria): List<Question>
    fun optimizePaperDifficulty(questions: List<Question>, targetDifficulty: Double): List<Question>
    fun ensureKnowledgePointCoverage(questions: List<Question>, requiredPoints: List<String>): List<Question>
}
```

## 📊 六、数据传输对象 (DTOs)

### 6.1 用户相关DTOs
```kotlin
data class CreateUserDto(
    val username: String,
    val email: String,
    val password: String,
    val displayName: String?,
    val role: String
)

data class UpdateUserDto(
    val email: String?,
    val displayName: String?,
    val avatarUrl: String?
)

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val role: String,
    val createdAt: LocalDateTime
)
```

### 6.2 课程相关DTOs
```kotlin
data class CreateCourseDto(
    val name: String,
    val description: String?,
    val subject: String,
    val grade: String?,
    val teacherId: UUID
)

data class CourseDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val subject: String,
    val grade: String?,
    val teacher: UserDto,
    val studentCount: Int,
    val createdAt: LocalDateTime
)
```

### 6.3 题目相关DTOs
```kotlin
data class CreateQuestionDto(
    val title: String,
    val content: String,
    val type: QuestionType,
    val difficulty: Difficulty,
    val knowledgePoints: Set<String>,
    val answer: String?,
    val explanation: String?,
    val courseId: UUID?
)

data class QuestionDto(
    val id: UUID,
    val title: String,
    val content: String,
    val type: QuestionType,
    val difficulty: Difficulty,
    val knowledgePoints: Set<String>,
    val answer: String?,
    val explanation: String?,
    val course: CourseDto?,
    val creator: UserDto,
    val createdAt: LocalDateTime
)
```

## 🗃️ 七、Repository接口

### 7.1 课程Repository
```kotlin
@Repository
interface CourseRepository : JpaRepository<Course, UUID> {
    fun findByTeacherId(teacherId: UUID): List<Course>
    fun findByStudentsContaining(student: User): List<Course>
    fun findBySubject(subject: String): List<Course>
    fun findByNameContainingIgnoreCase(name: String): List<Course>
}
```

### 7.2 题目Repository
```kotlin
@Repository
interface QuestionRepository : JpaRepository<Question, UUID> {
    fun findByCourseId(courseId: UUID): List<Question>
    fun findByType(type: QuestionType): List<Question>
    fun findByDifficulty(difficulty: Difficulty): List<Question>
    fun findByKnowledgePointsContaining(knowledgePoint: String): List<Question>
    
    @Query("SELECT q FROM Question q WHERE q.knowledgePoints IN :points AND q.difficulty = :difficulty")
    fun findByKnowledgePointsAndDifficulty(points: List<String>, difficulty: Difficulty): List<Question>
}
```

### 7.3 作业Repository
```kotlin
@Repository
interface AssignmentRepository : JpaRepository<Assignment, UUID> {
    fun findByCourseId(courseId: UUID): List<Assignment>
    fun findByTeacherId(teacherId: UUID): List<Assignment>
    fun findByStatus(status: AssignmentStatus): List<Assignment>
    fun findByDueDateBefore(date: LocalDateTime): List<Assignment>
}

@Repository  
interface AssignmentSubmissionRepository : JpaRepository<AssignmentSubmission, UUID> {
    fun findByAssignmentId(assignmentId: UUID): List<AssignmentSubmission>
    fun findByStudentId(studentId: UUID): List<AssignmentSubmission>
    fun findByAssignmentIdAndStudentId(assignmentId: UUID, studentId: UUID): AssignmentSubmission?
    fun countByAssignmentId(assignmentId: UUID): Long
}
```

## 📋 八、数据库迁移脚本

### ✅ 已完成的迁移脚本
- `V1__initial_schema.sql` - 完整的核心业务表结构
- `V2__create_storage_objects_table.sql` - 文件存储表
- `V3__add_embedding_status_to_storage_objects.sql` - 添加嵌入状态字段  
- `V4__insert_test_user.sql` - 测试用户数据

### 可能需要的额外迁移
- 索引优化脚本
- 权限相关表 (如果需要更细粒度的权限控制)
- 通知表 (当前schema中缺失)
- 菜单配置表

## 🚀 实施优先级建议

> **重要发现**: 数据库Schema已经完整设计好，主要任务是实现Kotlin业务层代码

### Phase 1: 核心实体和Repository (1-2周)
1. **Domain实体实现** - Course, Question, Assignment, KnowledgePoint等实体类
2. **Repository接口** - 对应的JPA Repository接口
3. **认证授权系统** - AuthController + Spring Security配置
4. **用户管理增强** - UserController + UserService完整实现

### Phase 2: 核心业务功能 (2-3周)  
1. **课程管理** - CourseController + CourseService
2. **题目管理** - QuestionController + QuestionService
3. **作业管理** - AssignmentController + AssignmentService
4. **提交批改** - 作业提交和批改功能

### Phase 3: 高级功能 (2-3周)
1. **学情分析** - AnalysisController + AnalysisService
2. **统计报表** - StatsController 
3. **教学大纲** - 对应LessonPlan表的功能实现
4. **通知系统** - NotificationController (需要先添加通知表)

### Phase 4: AI智能增强 (2-3周)
1. **AI备课生成** - 基于现有LessonPlan和DocumentChunk表
2. **智能组卷** - 基于Question和KnowledgePoint
3. **个性化推荐** - 学习资源推荐算法
4. **RAG功能增强** - 利用现有DocumentChunk表

### Phase 5: 系统完善 (1-2周)
1. **菜单管理** - MenuController (可能需要添加菜单表)
2. **性能优化** - 缓存、查询优化
3. **监控告警** - 系统健康监控
4. **文档完善** - API文档、部署文档

## 📝 总结

**好消息**: 数据库设计已经非常完善，包含了PRD需求的所有核心功能表结构。

**主要任务**: 将已设计的数据库表结构转换为Kotlin代码实现:
- 13个核心业务表 → 对应的Domain实体类
- Repository接口实现
- Service业务逻辑层
- Controller REST API层
- DTO数据传输对象

**预计时间**: 8-12周 (比预期减少了3-4周，因为数据库设计已完成)

**关键成功因素**:
- 严格按照现有数据库Schema实现实体类
- 保持与现有代码架构的一致性  
- 重复利用现有的基础设施(OSS、AI、消息队列)
- 及时完善单元测试和集成测试
# Janus 后端缺失功能 - 快速概览

## 重要发现 🎉
✅ **数据库Schema已完整设计** - V1__initial_schema.sql包含所有核心业务表  
✅ **基础设施完备** - 文件存储(OSS) + AI嵌入向量 + 用户模型  
❌ **主要缺失** - 将数据库表转换为Kotlin业务代码

## 数据库 vs 代码实现对比
| 数据库表 | Kotlin实体 | Repository | Service | Controller |
|---------|-----------|------------|---------|------------|
| janus_users ✅ | User ✅ | UserRepository ✅ | ❌ | ❌ |
| janus_courses ✅ | Course ✅ | CourseRepository ✅ | CourseService ✅ | CourseController ✅ |
| janus_questions ✅ | Question ✅ | QuestionRepository ✅ | QuestionService ✅ | QuestionController ✅ |
| janus_assignments ✅ | Assignment ✅ | AssignmentRepository ✅ | AssignmentService ✅ | AssignmentController ✅ |
| janus_knowledge_points ✅ | KnowledgePoint ✅ | KnowledgePointRepository ✅ | ❌ | ❌ |
| janus_lesson_plans ✅ | LessonPlan ✅ | LessonPlanRepository ✅ | ❌ | ❌ |
| janus_notifications ✅ | Notification ✅ | NotificationRepository ✅ | NotificationService ✅ | NotificationController ✅ |
| janus_storage_objects ✅ | StorageObject ✅ | StorageObjectRepository ✅ | OssService ✅ | StorageController ✅ |

## 急需实现的业务层代码

### 优先级1: Domain实体类 (基于已有数据库表) ✅ 已完成
- Course - 课程实体 (对应janus_courses表) ✅
- Question - 题目实体 (对应janus_questions表) ✅  
- Assignment - 作业实体 (对应janus_assignments表) ✅
- AssignmentSubmission - 作业提交 (对应janus_assignment_submissions表) ✅
- KnowledgePoint - 知识点 (对应janus_knowledge_points表) ✅
- LessonPlan - 教学计划 (对应janus_lesson_plans表) ✅
- Notification - 通知 (对应janus_notifications表) ✅

### 优先级2: Repository接口 ✅ 已完成
- CourseRepository ✅
- QuestionRepository ✅  
- AssignmentRepository ✅
- AssignmentSubmissionRepository ✅
- KnowledgePointRepository ✅
- LessonPlanRepository ✅
- NotificationRepository ✅

### 优先级3: Service层 ✅ 已完成
- CourseService ✅
- QuestionService ✅
- AssignmentService ✅
- NotificationService ✅
- UserService ❌ (需增强现有功能)

### 优先级4: Controller层 ✅ 已完成
- CourseController ✅
- QuestionController ✅
- AssignmentController ✅
- NotificationController ✅
- AuthController ❌ (下一优先级)
- UserController ❌ (增强现有功能)

### 下一步计划 🎯
- 认证授权系统 (AuthController + Spring Security)
- 用户管理增强 (UserController)
- 教学计划管理 (LessonPlanController)
- 知识点管理 (KnowledgePointController)
1. `AuthController` - 登录认证 (最高优先级)
2. `UserController` - 用户管理完善
3. `CourseController` - 课程管理  
4. `QuestionController` - 题目管理
5. `AssignmentController` - 作业管理
6. `AnalysisController` - 学情分析

## 技术债务
- 缺少Spring Security配置
- 缺少JWT Token管理
- 缺少角色权限控制
- 缺少数据库迁移脚本
- 缺少完整的Service层
- 缺少Repository层接口

## 实施建议
**第一优先级**: 实体类 + Repository接口 (1-2周)  
**第二优先级**: 认证 + 核心Controller (2-3周)  
**第三优先级**: 高级功能 + AI增强 (2-3周)

**预计总时间**: 5-8周 (比预期减少，因为数据库设计已完成)

详细信息请查看 [完整TODO文档](./TODO.md)
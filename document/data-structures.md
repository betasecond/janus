# 数据结构定义文档

## 概述

本文档定义了 Janus Eye 教学平台中使用的所有数据结构和类型定义。这些数据结构基于《Janus Eye 教学平台统一 API 规范 (v1.0)》，采用 VO (View Object) 模式，用于前端与后端的数据交互。

## 全局响应结构

### ApiResponse<T> - 统一成功响应
```typescript
interface ApiResponse<T> {
  success: true;
  data: T; // T 可以是任何数据类型，如 UserVO, PageVO<CourseVO> 等
}
```

### ApiErrorResponse - 统一错误响应
```typescript
interface ApiErrorResponse {
  success: false;
  error: {
    code: string;    // 错误代码，如 "RESOURCE_NOT_FOUND"
    message: string; // 错误描述信息
  };
}
```

### PageVO<T> - 分页数据结构
```typescript
interface PageVO<T> {
  content: T[];         // 当前页的数据列表
  totalElements: number; // 总元素数量
  totalPages: number;    // 总页数
  size: number;          // 每页大小
  number: number;        // 当前页码 (从 0 开始)
}
```

## 核心数据结构

### 1. 用户相关 (User Related)

#### UserVO - 用户视图对象
```typescript
interface UserVO {
  id: string;           // 用户唯一标识符
  displayName: string;  // 用户显示名称
  email: string;        // 用户邮箱
  avatarUrl: string;    // 用户头像 URL
  role: 'TEACHER' | 'STUDENT' | 'ADMIN';  // 用户角色
}
```

**字段说明**：
- `id`: 用户的唯一标识符，UUID 格式
- `displayName`: 用户的显示名称（原 `name` 字段）
- `email`: 用户的邮箱地址，用于登录和身份认证
- `avatarUrl`: 用户头像图片的 URL 地址（原 `avatar` 字段）
- `role`: 用户角色类型，枚举值统一为大写格式

### 2. 课程相关 (Course Related)

#### CourseVO - 课程视图对象
```typescript
interface CourseVO {
  id: string;           // 课程唯一标识符
  name: string;         // 课程名称
  description: string;  // 课程描述
  teacher: UserVO;      // 授课教师信息
  coverImageUrl?: string; // 课程封面图片 URL（可选）
}
```

**字段说明**：
- `id`: 课程的唯一标识符，UUID 格式
- `name`: 课程的名称
- `description`: 课程的详细描述
- `teacher`: 授课教师的完整用户信息对象（原 `teacher: string` 字段）
- `coverImageUrl`: 课程封面图片 URL（原 `image` 字段）

**注意**：学生数量和课程进度不再作为课程的直接属性，需要通过相应的 API 端点获取：
- 学生列表：`GET /api/v1/courses/{courseId}/enrollments`
- 课程进度：通过学情分析服务获取

#### CourseEnrollmentVO - 课程选课视图对象
```typescript
interface CourseEnrollmentVO {
  courseId: string;     // 课程 ID
  studentId: string;    // 学生 ID
  enrolledAt: string;   // 选课时间
}
```

### 3. 题目相关 (Question Related)

#### QuestionVO - 题目视图对象
```typescript
interface QuestionVO {
  id: string;                    // 题目唯一标识符
  content: string;               // 题目内容
  type: 'SINGLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER' | 'ESSAY';  // 题目类型
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';  // 题目难度
  knowledgePointIds: string[];   // 知识点 ID 列表
  options?: Record<string, string>;  // 选项对象（选择题使用）
  correctAnswer?: string;        // 正确答案
  explanation?: string;          // 答案解释
}
```

**字段说明**：
- `id`: 题目的唯一标识符，UUID 格式
- `content`: 题目的完整内容描述（原 `title` 字段）
- `type`: 题目类型，枚举值统一为大写格式
- `difficulty`: 题目难度等级，枚举值统一为大写格式
- `knowledgePointIds`: 该题目关联的知识点 ID 数组（原 `knowledgePoints` 字段）
- `options`: 选择题的选项键值对对象，如 `{ "A": "选项A", "B": "选项B" }`（原 `options: string[]`）
- `correctAnswer`: 正确答案，对应选项的键（如 "B"）
- `explanation`: 答案解释说明（可选）

#### KnowledgePointVO - 知识点视图对象
```typescript
interface KnowledgePointVO {
  id: string;           // 知识点唯一标识符
  name: string;         // 知识点名称
  description?: string; // 知识点描述
  subject: string;      // 所属学科
  parentId?: string;    // 父知识点 ID（用于构建知识树）
}
```

### 4. 作业相关 (Assignment Related)

#### AssignmentVO - 作业视图对象
```typescript
interface AssignmentVO {
  id: string;           // 作业唯一标识符
  title: string;        // 作业标题
  description: string;  // 作业描述
  dueDate: string;      // 截止日期
  courseId: string;     // 所属课程 ID
  questionIds: string[]; // 题目 ID 列表
  createdAt: string;    // 创建时间
}
```

**字段说明**：
- `id`: 作业的唯一标识符，UUID 格式
- `title`: 作业的标题
- `description`: 作业的详细描述
- `dueDate`: 作业截止日期，ISO 8601 格式
- `courseId`: 作业所属课程的 ID
- `questionIds`: 作业包含的题目 ID 列表（原 `questions: Question[]` 字段）
- `createdAt`: 作业创建时间

**注意**：提交人数和满分分值不再作为作业的直接属性，需要通过相应的 API 端点获取：
- 提交详情：`GET /api/v1/assignments/{id}/submissions`
- 完整题目信息需要另行获取

#### AssignmentSubmissionVO - 作业提交视图对象
```typescript
interface AssignmentSubmissionVO {
  id: string;           // 提交记录唯一标识符
  assignmentId: string; // 作业 ID
  studentId: string;    // 学生 ID
  submittedAt: string;  // 提交时间
  status: 'SUBMITTED' | 'GRADING' | 'GRADED'; // 提交状态
  score?: number;       // 得分（可选）
  answers: SubmissionAnswerVO[]; // 答案列表
}
```

#### SubmissionAnswerVO - 提交答案视图对象
```typescript
interface SubmissionAnswerVO {
  id: string;           // 答案记录唯一标识符
  questionId: string;   // 题目 ID
  answer: any;          // 学生答案（类型根据题目类型而定）
  isCorrect?: boolean;  // 是否正确（可选）
}
```

### 5. 通知相关 (Notification Related)

#### NotificationVO - 通知视图对象
```typescript
interface NotificationVO {
  id: string;           // 通知唯一标识符
  title: string;        // 通知标题
  content: string;      // 通知内容
  type: 'INFO' | 'WARNING' | 'SUCCESS' | 'ERROR';  // 通知类型
  isRead: boolean;      // 是否已读
  createdAt: string;    // 创建时间
  senderId?: string;    // 发送者 ID（可选）
}
```

**字段说明**：
- `id`: 通知的唯一标识符，UUID 格式
- `title`: 通知的标题
- `content`: 通知的详细内容
- `type`: 通知类型，枚举值统一为大写格式
- `isRead`: 标记通知是否已被用户阅读
- `createdAt`: 通知创建时间，ISO 8601 格式
- `senderId`: 发送通知者的用户 ID（原 `avatar` 字段改为通过用户 ID 关联）

#### ToastNotificationVO - 浮窗通知视图对象
```typescript
interface ToastNotificationVO {
  id: string;           // 通知 ID（改为 string 类型保持一致性）
  title: string;        // 通知标题
  content: string;      // 通知内容
  type: 'SUCCESS' | 'ERROR' | 'INFO' | 'WARNING';  // 通知类型
}
```

### 6. 统计数据相关 (Statistics Related)

#### PerformanceStatsVO - 性能统计数据视图对象
```typescript
interface PerformanceStatsVO {
  studentId: string;              // 学生 ID
  courseId?: string;              // 课程 ID（可选，用于课程级别统计）
  averageAccuracy: number;        // 平均准确率
  frequentlyMissedConceptIds: string[];  // 频繁出错的知识点 ID 列表
  classRanking: number;           // 班级排名（数字）
  knowledgePointMastery: KnowledgePointMasteryVO[]; // 知识点掌握情况
  accuracyTrends: AccuracyTrendVO[]; // 准确率趋势
  questionTypeDistribution: QuestionTypeDistributionVO[]; // 题型分布
  generatedAt: string;            // 统计生成时间
}
```

#### KnowledgePointMasteryVO - 知识点掌握情况视图对象
```typescript
interface KnowledgePointMasteryVO {
  knowledgePointId: string;       // 知识点 ID
  knowledgePointName: string;     // 知识点名称
  masteryLevel: number;           // 掌握程度 (0-100)
  totalQuestions: number;         // 总题目数
  correctAnswers: number;         // 正确答案数
}
```

#### AccuracyTrendVO - 准确率趋势视图对象
```typescript
interface AccuracyTrendVO {
  period: string;                 // 时间周期（如 "2024-W01"）
  accuracy: number;               // 准确率
  totalQuestions: number;         // 总题目数
  correctAnswers: number;         // 正确答案数
}
```

#### QuestionTypeDistributionVO - 题型分布视图对象
```typescript
interface QuestionTypeDistributionVO {
  type: string;                   // 题目类型
  count: number;                  // 题目数量
  percentage: number;             // 百分比
  averageAccuracy: number;        // 该题型的平均准确率
}
```

### 7. 教学大纲相关 (Syllabus Related)

#### LessonPlanVO - 教案视图对象
```typescript
interface LessonPlanVO {
  id: string;           // 教案唯一标识符
  name: string;         // 教案名称
  courseId?: string;    // 课程 ID（可选）
  creatorId: string;    // 创建者 ID
  sourceDocumentUrl?: string; // 源文档 URL（可选）
  status: 'DRAFT' | 'GENERATING' | 'COMPLETED' | 'FAILED'; // 教案状态
  aiModelUsed?: string; // 使用的 AI 模型（可选）
  createdAt: string;    // 创建时间
  updatedAt: string;    // 更新时间
}
```

#### LessonPlanItemVO - 教案内容项视图对象
```typescript
interface LessonPlanItemVO {
  id: string;           // 内容项唯一标识符
  lessonPlanId: string; // 教案 ID
  title: string;        // 内容项标题
  contentType: 'LECTURE' | 'EXERCISE' | 'NOTE' | 'SUMMARY'; // 内容类型
  content: string;      // 内容详情
  order: number;        // 排序顺序
  createdAt: string;    // 创建时间
}
```

#### DocumentChunkVO - 文档内容块视图对象
```typescript
interface DocumentChunkVO {
  id: string;           // 内容块唯一标识符
  lessonPlanId: string; // 教案 ID
  content: string;      // 内容块文本
  chunkOrder: number;   // 块顺序
  vectorId?: string;    // 向量 ID（可选）
  metadata?: Record<string, any>; // 元数据（可选）
  createdAt: string;    // 创建时间
}
```

### 8. 学生分析相关 (Student Analysis Related)

#### StudentAnalysisVO - 学生分析数据视图对象
```typescript
interface StudentAnalysisVO {
  id: string;               // 分析记录唯一标识符
  studentId: string;        // 学生 ID
  studentName: string;      // 学生姓名
  courseId?: string;        // 课程 ID（可选）
  assignmentId?: string;    // 作业 ID（可选）
  incorrectQuestionIds: string[]; // 错题 ID 列表
  errorPatterns: ErrorPatternVO[]; // 错误模式分析
  suggestedActions: SuggestedActionVO[]; // 建议改进措施
  analysisDate: string;     // 分析时间
  createdAt: string;        // 创建时间
}
```

#### ErrorPatternVO - 错误模式视图对象
```typescript
interface ErrorPatternVO {
  id: string;               // 错误模式唯一标识符
  knowledgePointId: string; // 相关知识点 ID
  errorType: string;        // 错误类型
  frequency: number;        // 出现频率
  description: string;      // 错误描述
  examples: string[];       // 错误示例
}
```

#### SuggestedActionVO - 建议措施视图对象
```typescript
interface SuggestedActionVO {
  id: string;               // 建议措施唯一标识符
  actionType: 'REVIEW' | 'PRACTICE' | 'TUTORIAL' | 'CONSULTATION'; // 措施类型
  priority: 'HIGH' | 'MEDIUM' | 'LOW'; // 优先级
  description: string;      // 措施描述
  resourceIds?: string[];   // 相关资源 ID 列表（可选）
  estimatedDuration?: number; // 预估耗时（分钟）
}
```

### 9. 导航菜单相关 (Navigation Menu Related)

#### MenuItemVO - 菜单项视图对象
```typescript
interface MenuItemVO {
  id: string;           // 菜单项唯一标识符
  label: string;        // 菜单标签
  icon: string;         // 图标名称
  path: string;         // 路由路径
  parentId?: string;    // 父菜单 ID（可选，用于多级菜单）
  order: number;        // 排序顺序
  isActive?: boolean;   // 是否激活状态（可选）
  isVisible: boolean;   // 是否可见
  requiredRole?: 'TEACHER' | 'STUDENT' | 'ADMIN'; // 所需角色（可选）
}
```

**字段说明**：
- `id`: 菜单项的唯一标识符，UUID 格式
- `label`: 菜单显示的文本标签
- `icon`: 菜单图标的名称或类名
- `path`: 菜单对应的路由路径
- `parentId`: 父菜单的 ID，用于构建多级菜单结构
- `order`: 菜单项的排序顺序
- `isActive`: 当前是否为激活状态（通常由前端路由状态决定）
- `isVisible`: 菜单项是否可见
- `requiredRole`: 访问该菜单项所需的用户角色

### 10. 资源管理相关 (Resource Management Related)

#### ResourceVO - 教学资源视图对象
```typescript
interface ResourceVO {
  id: string;           // 资源唯一标识符
  title: string;        // 资源标题
  type: string;         // 资源类型
  subject: string;      // 学科
  uploaderId: string;   // 上传者 ID
  createdAt: string;    // 创建时间
  storageObjectId: string; // 关联的存储对象 ID
  thumbnail?: string;   // 缩略图 URL（可选）
}
```

**字段说明**：
- `id`: 资源的唯一标识符，UUID 格式
- `title`: 资源的标题
- `type`: 资源类型（具体枚举值根据业务需求定义）
- `subject`: 资源所属学科
- `uploaderId`: 上传者的用户 ID（原 `uploader: string` 字段）
- `createdAt`: 资源创建时间，ISO 8601 格式（原 `uploadDate` 字段）
- `storageObjectId`: 关联的底层存储对象 ID，用于获取实际文件信息
- `thumbnail`: 资源缩略图 URL（可选）

#### StorageObjectVO - 存储对象视图对象
```typescript
interface StorageObjectVO {
  id: string;           // 存储对象唯一标识符
  objectKey: string;    // 在对象存储服务中的路径
  url: string;          // 文件的可访问 URL
  originalFilename: string; // 原始文件名
  contentType: string;  // 文件 MIME 类型
  fileSize: number;     // 文件大小（字节）
  uploaderId: string;   // 上传者 ID
  createdAt: string;    // 上传时间
}
```

**字段说明**：
- `id`: 存储对象的唯一标识符，UUID 格式
- `objectKey`: 文件在对象存储服务（如 S3, OSS）中的唯一路径
- `url`: 文件的可直接访问 URL
- `originalFilename`: 用户上传时的原始文件名
- `contentType`: 文件的 MIME 类型（如 "image/jpeg", "application/pdf"）
- `fileSize`: 文件大小，以字节为单位
- `uploaderId`: 上传该文件的用户 ID
- `createdAt`: 文件上传时间，ISO 8601 格式

**注意**：新架构将"存储对象"和"教学资源"分离：
- `StorageObjectVO` 代表物理文件，关注存储和访问
- `ResourceVO` 代表教学资源，关注教学意义和业务逻辑
- 一个教学资源通过 `storageObjectId` 关联到具体的存储对象

## 数据类型说明

### 基础类型
- `string`: 字符串类型，所有 ID 字段统一使用 UUID 格式
- `number`: 数字类型
- `boolean`: 布尔类型
- `Date`: 日期类型（统一以 ISO 8601 格式的字符串传输）

### 联合类型（枚举值统一为大写）
- `'TEACHER' | 'STUDENT' | 'ADMIN'`: 用户角色枚举
- `'SINGLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER' | 'ESSAY'`: 题目类型枚举
- `'EASY' | 'MEDIUM' | 'HARD'`: 难度等级枚举
- `'INFO' | 'WARNING' | 'SUCCESS' | 'ERROR'`: 通知类型枚举
- `'DRAFT' | 'GENERATING' | 'COMPLETED' | 'FAILED'`: 教案状态枚举
- `'SUBMITTED' | 'GRADING' | 'GRADED'`: 作业提交状态枚举

### 数组类型
- `string[]`: 字符串数组，通常用于 ID 列表
- `QuestionVO[]`: 题目视图对象数组
- `LessonPlanItemVO[]`: 教案内容项数组
- `SubmissionAnswerVO[]`: 提交答案数组

### 对象类型
- `Record<string, string>`: 键值对对象，如题目选项 `{ "A": "选项A", "B": "选项B" }`
- `Record<string, any>`: 通用键值对对象，用于元数据存储
- `ApiResponse<T>`: 泛型响应包装器
- `PageVO<T>`: 泛型分页数据结构

## 使用说明

1. **导入方式**：所有类型定义都从 `@/types` 模块导入
2. **命名规范**：使用 PascalCase 命名接口，使用 camelCase 命名字段
3. **可选字段**：使用 `?` 标记可选字段
4. **日期格式**：所有日期字段使用 ISO 8601 格式字符串
5. **ID 格式**：所有 ID 字段使用字符串类型，推荐使用 UUID

## 扩展说明

这些数据结构是基于当前系统需求设计的，在实际开发过程中可能需要根据业务需求进行调整和扩展。建议在修改数据结构时：

1. 保持向后兼容性
2. 更新相关的 API 接口
3. 同步更新文档
4. 进行充分的测试

## 后端数据库表结构 (Backend Database Schema)

以下是为支持 Janus Eye 功能而设计的后端 PostgreSQL 数据库表结构。

### 1. `janus_users` - 用户表
存储所有角色的用户信息。
```sql
CREATE TABLE janus_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    avatar_url VARCHAR(512),
    role VARCHAR(20) NOT NULL CHECK (role IN ('teacher', 'student', 'admin')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 2. `janus_courses` - 课程表
存储课程基本信息。
```sql
CREATE TABLE janus_courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    teacher_id UUID NOT NULL REFERENCES janus_users(id),
    cover_image_url VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 3. `janus_course_enrollments` - 课程注册表
连接学生和课程，表示学生选课关系。
```sql
CREATE TABLE janus_course_enrollments (
    course_id UUID NOT NULL REFERENCES janus_courses(id),
    student_id UUID NOT NULL REFERENCES janus_users(id),
    enrolled_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (course_id, student_id)
);
```

### 4. `janus_knowledge_points` - 知识点表 (新增)
存储结构化的知识点信息，用于构建知识图谱。
```sql
CREATE TABLE janus_knowledge_points (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    subject VARCHAR(100),
    parent_id UUID REFERENCES janus_knowledge_points(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 5. `janus_questions` - 题库表 (修改)
存储所有题目信息，作为资源库。移除了 `knowledge_points` 字段，改用关联表。
```sql
CREATE TABLE janus_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL CHECK (type IN ('multiple-choice', 'true-false', 'short-answer', 'essay')),
    difficulty VARCHAR(20) NOT NULL CHECK (difficulty IN ('easy', 'medium', 'hard')),
    content JSONB NOT NULL, -- 存储题干、选项等
    correct_answer JSONB,   -- 存储正确答案
    explanation TEXT,
    creator_id UUID REFERENCES janus_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```
**`content` JSONB 示例 (选择题):**
```json
{ "answer": "1" }
```

### 6. `janus_question_knowledge_points` - 题目知识点关联表 (新增)
连接题目和知识点，实现多对多关系。
```sql
CREATE TABLE janus_question_knowledge_points (
    question_id UUID NOT NULL REFERENCES janus_questions(id) ON DELETE CASCADE,
    knowledge_point_id UUID NOT NULL REFERENCES janus_knowledge_points(id) ON DELETE CASCADE,
    PRIMARY KEY (question_id, knowledge_point_id)
);
```

### 7. `janus_assignments` - 作业表
存储教师发布的作业信息。
```sql
CREATE TABLE janus_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    course_id UUID NOT NULL REFERENCES janus_courses(id),
    creator_id UUID NOT NULL REFERENCES janus_users(id),
    due_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 8. `janus_assignment_questions` - 作业题目关联表
连接作业和题目。
```sql
CREATE TABLE janus_assignment_questions (
    assignment_id UUID NOT NULL REFERENCES janus_assignments(id),
    question_id UUID NOT NULL REFERENCES janus_questions(id),
    "order" INTEGER,
    PRIMARY KEY (assignment_id, question_id)
);
```

### 9. `janus_assignment_submissions` - 作业提交表
存储学生的作业提交记录。
```sql
CREATE TABLE janus_assignment_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID NOT NULL REFERENCES janus_assignments(id),
    student_id UUID NOT NULL REFERENCES janus_users(id),
    submitted_at TIMESTAMPTZ DEFAULT NOW(),
    status VARCHAR(20) DEFAULT 'submitted' CHECK (status IN ('submitted', 'grading', 'graded')),
    score NUMERIC(5, 2),
    UNIQUE(assignment_id, student_id)
);
```

### 10. `janus_submission_answers` - 提交答案表
存储学生提交的具体答案。
```sql
CREATE TABLE janus_submission_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID NOT NULL REFERENCES janus_assignment_submissions(id),
    question_id UUID NOT NULL REFERENCES janus_questions(id),
    answer JSONB,
    is_correct BOOLEAN
);
```

### 11. `janus_lesson_plans` - 备课（教案）表 (修改)
存储教师通过 AI 或手动创建的备课信息。增加了AI生成相关的元数据字段。
```sql
CREATE TABLE janus_lesson_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    course_id UUID REFERENCES janus_courses(id),
    creator_id UUID NOT NULL REFERENCES janus_users(id),
    source_document_url VARCHAR(512),
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'generating', 'completed', 'failed')),
    ai_model_used VARCHAR(100),
    ai_prompt TEXT,
    generation_duration_ms BIGINT,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 12. `janus_document_chunks` - 文档内容块表 (新增)
存储为 RAG 流程分割的文档内容块及其向量元数据。
```sql
CREATE TABLE janus_document_chunks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lesson_plan_id UUID NOT NULL REFERENCES janus_lesson_plans(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    chunk_order INTEGER NOT NULL,
    vector_id VARCHAR(255) UNIQUE,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 13. `janus_lesson_plan_items` - 教案内容项表
存储教案的具体章节或内容块。
```sql
CREATE TABLE janus_lesson_plan_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lesson_plan_id UUID NOT NULL REFERENCES janus_lesson_plans(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL CHECK (content_type IN ('lecture', 'exercise', 'note', 'summary')),
    content TEXT,
    "order" INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 14. `janus_storage_objects` - 对象存储元数据表 (新增)
存储上传到对象存储（如OSS）的文件的元数据。
```sql
CREATE TABLE janus_storage_objects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_key VARCHAR(1024) NOT NULL UNIQUE,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(128),
    storage_provider VARCHAR(50) NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    uploader_id UUID REFERENCES janus_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

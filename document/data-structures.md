# 数据结构定义文档

## 概述

本文档定义了 Janus Eye 教学平台中使用的所有数据结构和类型定义。这些数据结构用于前端与后端的数据交互，以及内部数据处理。

## 核心数据结构

### 1. 用户相关 (User Related)

#### User - 用户信息
```typescript
interface User {
  id: string;           // 用户唯一标识符
  name: string;         // 用户姓名
  email: string;        // 用户邮箱
  avatar: string;       // 用户头像 URL
  role: 'teacher' | 'student' | 'admin';  // 用户角色
}
```

**字段说明**：
- `id`: 用户的唯一标识符，通常为 UUID 格式
- `name`: 用户的真实姓名
- `email`: 用户的邮箱地址，用于登录和通知
- `avatar`: 用户头像图片的 URL 地址
- `role`: 用户角色类型，支持教师、学生、管理员三种角色

### 2. 课程相关 (Course Related)

#### Course - 课程信息
```typescript
interface Course {
  id: string;           // 课程唯一标识符
  name: string;         // 课程名称
  description: string;  // 课程描述
  teacher: string;      // 授课教师姓名
  students: number;     // 学生人数
  progress: number;     // 课程进度（0-100）
  image?: string;       // 课程封面图片 URL（可选）
}
```

**字段说明**：
- `id`: 课程的唯一标识符
- `name`: 课程的名称
- `description`: 课程的详细描述
- `teacher`: 授课教师的姓名
- `students`: 参与课程的学生数量
- `progress`: 课程完成进度，范围为 0-100
- `image`: 课程封面图片，可选字段

### 3. 题目相关 (Question Related)

#### Question - 题目信息
```typescript
interface Question {
  id: string;                    // 题目唯一标识符
  title: string;                 // 题目标题
  type: 'multiple-choice' | 'true-false' | 'short-answer' | 'essay';  // 题目类型
  difficulty: 'easy' | 'medium' | 'hard';  // 题目难度
  knowledgePoints: string[];     // 知识点列表
  options?: string[];            // 选项列表（选择题使用）
  correctAnswer?: string | number;  // 正确答案
  explanation?: string;          // 答案解释
}
```

**字段说明**：
- `id`: 题目的唯一标识符
- `title`: 题目的标题或问题描述
- `type`: 题目类型，支持单选题、判断题、简答题、论述题
- `difficulty`: 题目难度等级
- `knowledgePoints`: 该题目涉及的知识点数组
- `options`: 选择题的选项数组（可选）
- `correctAnswer`: 正确答案（可选）
- `explanation`: 答案解释说明（可选）

### 4. 作业相关 (Assignment Related)

#### Assignment - 作业信息
```typescript
interface Assignment {
  id: string;           // 作业唯一标识符
  title: string;        // 作业标题
  description: string;  // 作业描述
  dueDate: string;      // 截止日期
  courseId: string;     // 所属课程 ID
  questions: Question[]; // 题目列表
  submissions: number;  // 提交人数
  maxScore: number;     // 满分分值
}
```

**字段说明**：
- `id`: 作业的唯一标识符
- `title`: 作业的标题
- `description`: 作业的详细描述
- `dueDate`: 作业截止日期，ISO 8601 格式
- `courseId`: 作业所属课程的 ID
- `questions`: 作业包含的题目列表
- `submissions`: 已提交作业的学生数量
- `maxScore`: 作业的满分分值

### 5. 通知相关 (Notification Related)

#### Notification - 通知信息
```typescript
interface Notification {
  id: string;           // 通知唯一标识符
  title: string;        // 通知标题
  content: string;      // 通知内容
  type: 'info' | 'warning' | 'success' | 'error';  // 通知类型
  isRead: boolean;      // 是否已读
  createdAt: string;    // 创建时间
  avatar?: string;      // 发送者头像 URL（可选）
}
```

**字段说明**：
- `id`: 通知的唯一标识符
- `title`: 通知的标题
- `content`: 通知的详细内容
- `type`: 通知类型，用于显示不同的样式
- `isRead`: 标记通知是否已被用户阅读
- `createdAt`: 通知创建时间，ISO 8601 格式
- `avatar`: 发送通知者的头像 URL（可选）

#### ToastNotification - 浮窗通知
```typescript
interface ToastNotification {
  id: number;           // 通知 ID
  title: string;        // 通知标题
  content: string;      // 通知内容
  type: 'success' | 'error' | 'info' | 'warning';  // 通知类型
}
```

### 6. 统计数据相关 (Statistics Related)

#### PerformanceStats - 性能统计数据
```typescript
interface PerformanceStats {
  averageAccuracy: number;        // 平均准确率
  frequentlyMissedConcepts: string[];  // 频繁出错的概念
  classRanking: string;           // 班级排名
  knowledgePointMastery: {        // 知识点掌握情况
    [key: string]: number;
  };
  accuracyTrends: {               // 准确率趋势
    week: string;
    accuracy: number;
  }[];
  questionTypeDistribution: {     // 题型分布
    type: string;
    percentage: number;
  }[];
}
```

**字段说明**：
- `averageAccuracy`: 学生的平均答题准确率
- `frequentlyMissedConcepts`: 学生经常出错的概念列表
- `classRanking`: 学生在班级中的排名
- `knowledgePointMastery`: 各知识点的掌握程度映射
- `accuracyTrends`: 按周统计的准确率趋势数据
- `questionTypeDistribution`: 不同题型的分布百分比

### 7. 教学大纲相关 (Syllabus Related)

#### Chapter - 章节信息
```typescript
interface Chapter {
  id: string;           // 章节唯一标识符
  title: string;        // 章节标题
  order: number;        // 章节顺序
  content: string;      // 章节内容
  exercises: Question[]; // 练习题列表
  isCompleted: boolean; // 是否完成
}
```

#### Syllabus - 教学大纲
```typescript
interface Syllabus {
  id: string;           // 大纲唯一标识符
  courseId: string;     // 课程 ID
  chapters: Chapter[];  // 章节列表
  isGenerating: boolean; // 是否正在生成
  progress: number;     // 生成进度
}
```

### 8. 学生分析相关 (Student Analysis Related)

#### StudentAnalysis - 学生分析数据
```typescript
interface StudentAnalysis {
  id: string;               // 分析记录唯一标识符
  studentName: string;      // 学生姓名
  incorrectQuestions: string; // 错题信息
  errorLocation: string;    // 错误位置
  suggestedCorrection: string; // 建议改正
}
```

### 9. 导航菜单相关 (Navigation Menu Related)

#### MenuItem - 菜单项
```typescript
interface MenuItem {
  id: string;           // 菜单项唯一标识符
  label: string;        // 菜单标签
  icon: string;         // 图标名称
  path: string;         // 路由路径
  isActive?: boolean;   // 是否激活状态（可选）
}
```

### 10. 资源管理相关 (Resource Management Related)

#### Resource - 资源信息
```typescript
interface Resource {
  id: string;           // 资源唯一标识符
  title: string;        // 资源标题
  type: 'video' | 'document' | 'image' | 'assessment';  // 资源类型
  subject: string;      // 学科
  uploader: string;     // 上传者
  uploadDate: string;   // 上传日期
  thumbnail?: string;   // 缩略图 URL（可选）
}
```

## 数据类型说明

### 基础类型
- `string`: 字符串类型
- `number`: 数字类型
- `boolean`: 布尔类型
- `Date`: 日期类型（通常以 ISO 8601 格式的字符串传输）

### 联合类型
- `'teacher' | 'student' | 'admin'`: 用户角色枚举
- `'multiple-choice' | 'true-false' | 'short-answer' | 'essay'`: 题目类型枚举
- `'easy' | 'medium' | 'hard'`: 难度等级枚举
- `'info' | 'warning' | 'success' | 'error'`: 通知类型枚举
- `'video' | 'document' | 'image' | 'assessment'`: 资源类型枚举

### 数组类型
- `string[]`: 字符串数组
- `Question[]`: 题目数组
- `Chapter[]`: 章节数组

### 对象类型
- `{ [key: string]: number }`: 键值对对象，键为字符串，值为数字

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
    lesson_plan_id UUID NOT NULL REFERENCES janus_lesson_plans(id),
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL CHECK (content_type IN ('lecture', 'exercise', 'note', 'summary')),
    content TEXT,
    "order" INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

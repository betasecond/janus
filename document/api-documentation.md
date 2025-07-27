# Janus Eye API 接口文档

本文档详细描述了 Janus Eye 教学平台的后端 API 接口，旨在为前端开发人员提供清晰、准确的对接指南。

**最后更新时间**: 2025-07-26

## 目录
- [通用数据结构](#通用数据结构)
- [1. 用户接口 (`UserController`)](#1-用户接口-usercontroller)
- [2. 课程接口 (`CourseController`)](#2-课程接口-coursecontroller)
- [3. 作业接口 (`AssignmentController`)](#3-作业接口-assignmentcontroller)
- [4. 题目接口 (`QuestionController`)](#4-题目接口-questioncontroller)
- [5. 通知接口 (`NotificationController`)](#5-通知接口-notificationcontroller)
- [6. 存储接口 (`StorageController`)](#6-存储接口-storagecontroller)
- [7. 资源接口 (`ResourceController`)](#7-资源接口-resourcecontroller)
- [8. AI 接口 (`EmbeddingController`)](#8-ai-接口-embeddingcontroller)
- [9. 统计接口 (`StatsController`)](#9-统计接口-statscontroller)
- [10. AI 接口 (`EmbeddingController`)](#10-ai-接口-embeddingcontroller)
- [11. 统计接口 (`StatsController`)](#11-统计接口-statscontroller)
- [12. 分析接口 (`AnalysisController`)](#12-分析接口-analysiscontroller)
- [13. 教学大纲接口 (`SyllabusController`)](#13-教学大纲接口-syllabuscontroller)
- [14. 菜单接口 (`MenuController`)](#14-菜单接口-menucontroller)

---

## 通用数据结构

### `UserVO`
代表一个用户的公开视图信息。

**Kotlin 定义**
```kotlin
data class UserVO(
    val id: UUID,
    val displayName: String?,
    val avatarUrl: String?,
    val role: String
)
```
**JSON 示例**
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "displayName": "John Doe",
  "avatarUrl": "https://example.com/avatar.png",
  "role": "STUDENT"
}
```

### `PageVO<T>`
用于分页查询的响应结构。

**Kotlin 定义**
```kotlin
data class PageVO<T>(
    val content: List<T>,      // 当前页数据
    val totalElements: Long, // 总元素数
    val totalPages: Int,     // 总页数
    val size: Int,           // 每页大小
    val number: Int          // 当前页码 (从0开始)
)
```
**JSON 示例 (以 `UserVO` 为例)**
```json
{
  "content": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "displayName": "John Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "STUDENT"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### `StatusVO`
通用的操作状态响应。

**Kotlin 定义**
```kotlin
data class StatusVO(
    val status: String,
    val message: String
)
```
**JSON 示例**
```json
{
  "status": "success",
  "message": "Operation completed successfully."
}
```

### `MessageVO`
通用的消息响应。

**Kotlin 定义**
```kotlin
data class MessageVO(
    val message: String
)
```
**JSON 示例**
```json
{
  "message": "All notifications marked as read"
}
```

---

## 1. 用户接口 (`UserController`)
**基础路径**: `/api/users`

### 1.1 获取所有用户
- **描述**: 获取系统中所有用户的列表
- **Endpoint**: `GET /api/users`
- **认证**: 需要
- **请求参数**: 无
- **响应**: `200 OK`
  - **Body**: `List<UserVO>`

### 1.2 根据ID获取用户
- **描述**: 根据用户ID获取特定用户的详细信息
- **Endpoint**: `GET /api/users/{id}`
- **认证**: 需要
- **请求参数**:
  - **路径参数**:
    - `id`: `UUID` (必需) - 用户ID
- **响应**:
  - `200 OK`: `UserVO`
  - `404 Not Found`: 如果用户不存在

---

## 2. 课程接口 (`CourseController`)
**基础路径**: `/api/courses`

### 1.1 获取性能统计
- **描述**: 获取平台整体的性能统计数据，例如平均正确率和常见错误概念。
- **Endpoint**: `GET /api/stats`
- **认证**: 需要
- **请求参数**: 无
- **响应**: `200 OK`
  - **Body**: `PerformanceStatsVO`
    **Kotlin 定义**
    ```kotlin
    data class PerformanceStatsVO(
        val averageAccuracy: Double,         // 平均正确率
        val frequentlyMissedConcepts: List<String> // 常见错误概念列表
    )
    ```
    **JSON 示例**
    ```json
    {
      "averageAccuracy": 0.95,
      "frequentlyMissedConcepts": ["数据结构", "算法"]
    }
    ```

---

## 2. 存储接口 (`StorageController`)
**基础路径**: `/api/storage`

### 2.1 上传文件
- **描述**: 上传一个文件到对象存储服务(OSS)。
- **Endpoint**: `POST /api/storage/upload`
- **认证**: 需要
- **请求参数**:
  - **类型**: `multipart/form-data`
  - **参数**:
    - `file`: `MultipartFile` (必需) - 要上传的文件。
    - `uploaderId`: `UUID` (必需) - 上传者的用户ID。
- **响应**: `200 OK`
  - **Body**: `StorageObjectVO`

### 2.2 获取文件详情
- **描述**: 根据文件ID获取文件的详细信息。
- **Endpoint**: `GET /api/storage/{id}`
- **认证**: 需要
- **请求参数**:
  - **路径参数**:
    - `id`: `UUID` (必需) - 文件ID。
- **响应**:
  - `200 OK`: `StorageObjectVO`
  - `404 Not Found`: 如果文件不存在。

### 2.3 请求文件向量化
- **描述**: 异步请求对已上传的文件进行向量化处理。
- **Endpoint**: `POST /api/storage/{id}/embed`
- **认证**: 需要
- **请求参数**:
  - **路径参数**:
    - `id`: `UUID` (必需) - 文件ID。
- **响应**: `202 Accepted`
  - **Body**: `StatusVO` (告知请求已被接受并排队处理)

### 2.4 获取文件列表 (分页)
- **描述**: 根据条件分页获取文件列表。普通用户默认只能看到自己上传的文件，管理员可以看到所有文件。
- **Endpoint**: `GET /api/storage`
- **认证**: 需要
- **查询参数**:
  - `currentUserId`: `UUID` (必需) - 当前操作用户的ID。**注意**: 这是一个临时的身份验证方案，在未来的版本中将被替换为基于令牌的安全上下文。
  - `uploaderId`: `UUID` (可选) - 按上传者ID筛选。仅管理员可用。
  - `keyword`: `String` (可选) - 按文件名进行模糊搜索。
  - `page`: `Int` (可选, 默认 `0`) - 页码。
  - `size`: `Int` (可选, 默认 `20`) - 每页大小。
- **响应**: `200 OK`
  - **Body**: `PageVO<StorageObjectVO>` (结构参考[通用数据结构](#PageVO<T>))

### `StorageObjectVO`
**Kotlin 定义**
```kotlin
data class StorageObjectVO(
    val id: UUID,
    val objectKey: String,
    val originalFilename: String,
    val fileSize: Long,
    val contentType: String?,
    val storageProvider: String,
    val bucketName: String,
    val embeddingStatus: EmbeddingStatus, // (NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED)
    val uploader: UserVO?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
```
**JSON 示例**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "objectKey": "uploads/myfile.pdf",
  "originalFilename": "myfile.pdf",
  "fileSize": 102400,
  "contentType": "application/pdf",
  "storageProvider": "minio",
  "bucketName": "janus",
  "embeddingStatus": "COMPLETED",
  "uploader": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "displayName": "Jane Doe",
    "avatarUrl": "https://example.com/avatar.png",
    "role": "TEACHER"
  },
  "createdAt": "2025-07-05T10:00:00Z",
  "updatedAt": "2025-07-05T10:05:00Z"
}
```

---

## 3. AI 接口 (`EmbeddingController`)
**基础路径**: `/ai`

### 3.1 文本向量化并存储
- **描述**: 将给定的文本进行向量化并存储到向量数据库。
- **Endpoint**: `POST /ai/embedding/store`
- **认证**: 需要
- **请求**:
  - **Body**: `EmbeddingRequest`
    **Kotlin 定义**
    ```kotlin
    data class EmbeddingRequest(
        val message: String // 需要向量化的文本
    )
    ```
    **JSON 示例**
    ```json
    {
      "message": "This is a text to be embedded."
    }
    ```
- **响应**: `200 OK`
  - **Body**: `StatusVO` (告知操作成功)

### 3.2 文本向量化
- **描述**: 对给定的文本进行向量化，直接返回向量结果。
- **Endpoint**: `POST /ai/embedding`
- **认证**: 需要
- **请求**:
  - **Body**: `EmbeddingRequest`
- **响应**: `200 OK`
  - **Body**: `EmbeddingVO`
    **Kotlin 定义**
    ```kotlin
    data class EmbeddingVO(
        // Spring AI 的 EmbeddingResponse，通常包含一个浮点数数组
        val embedding: EmbeddingResponse
    )
    ```
    **JSON 示例**
    ```json
    {
      "embedding": {
        "embedding": [0.1, 0.2, 0.3],
        "metadata": {}
      }
    }
    ```

---

## 4. 课程接口 (`CourseController`)
**基础路径**: `/api/courses`

### 4.1 创建课程
- **Endpoint**: `POST /api/courses`
- **请求 Body**: `CreateCourseDto`
  **Kotlin 定义**
  ```kotlin
  data class CreateCourseDto(
      val name: String,         // 课程名称
      val description: String?, // 课程描述
      val teacherId: UUID,      // 教师ID
      val coverImageUrl: String?  // 封面图片URL
  )
  ```
  **JSON 示例**
  ```json
  {
    "name": "Introduction to Kotlin",
    "description": "A comprehensive course on Kotlin programming.",
    "teacherId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "coverImageUrl": "https://example.com/course_cover.png"
  }
  ```
- **响应**: `201 Created` - `CourseVO`

### 4.2 获取课程详情
- **Endpoint**: `GET /api/courses/{id}`
- **路径参数**: `id: UUID` (课程ID)
- **响应**: `200 OK` - `CourseVO`

### 4.3 获取课程列表
- **Endpoint**: `GET /api/courses`
- **查询参数**:
  - `teacherId: UUID` (可选) - 按教师ID筛选
  - `studentId: UUID` (可选) - 按学生ID筛选
  - `keyword: String` (可选) - 按名称或描述搜索
- **响应**: `200 OK` - `List<CourseVO>`

### 4.4 更新课程
- **Endpoint**: `PUT /api/courses/{id}`
- **路径参数**: `id: UUID` (课程ID)
- **请求 Body**: `UpdateCourseDto`
  **Kotlin 定义**
  ```kotlin
  data class UpdateCourseDto(
      val name: String?,
      val description: String?,
      val coverImageUrl: String?
  )
  ```
  **JSON 示例**
  ```json
  {
    "name": "Advanced Kotlin",
    "description": "An advanced course on Kotlin programming and coroutines."
  }
  ```
- **响应**: `200 OK` - `CourseVO`

### 4.5 删除课程
- **Endpoint**: `DELETE /api/courses/{id}`
- **路径参数**: `id: UUID` (课程ID)
- **响应**: `204 No Content`

### 4.6 学生选课
- **Endpoint**: `POST /api/courses/{id}/enroll`
- **路径参数**: `id: UUID` (课程ID)
- **请求 Body**: `EnrollmentDto`
  **Kotlin 定义**
  ```kotlin
  data class EnrollmentDto(
      val studentId: UUID
  )
  ```
  **JSON 示例**
  ```json
  {
    "studentId": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12"
  }
  ```
- **响应**: `200 OK` - `CourseVO`

### 4.7 学生退课
- **Endpoint**: `DELETE /api/courses/{id}/enroll/{studentId}`
- **路径参数**:
  - `id: UUID` (课程ID)
  - `studentId: UUID` (学生ID)
- **响应**: `200 OK` - `CourseVO`

### 4.8 获取课程学生列表
- **Endpoint**: `GET /api/courses/{id}/students`
- **路径参数**: `id: UUID` (课程ID)
- **响应**: `200 OK` - `List<UserVO>`

### 4.9 获取课程统计
- **Endpoint**: `GET /api/courses/{id}/stats`
- **路径参数**: `id: UUID` (课程ID)
- **响应**: `200 OK` - `CourseStatsVO`

### 响应/请求体定义
- `CourseVO`
  **Kotlin 定义**
  ```kotlin
  data class CourseVO(
      val id: UUID,
      val name: String,
      val description: String?,
      val coverImageUrl: String?,
      val teacher: UserVO,
      val studentCount: Long, // 学生数量
      val createdAt: LocalDateTime?,
      val updatedAt: LocalDateTime?
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
    "name": "Introduction to Kotlin",
    "description": "A comprehensive course on Kotlin programming.",
    "coverImageUrl": "https://example.com/course_cover.png",
    "teacher": {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "displayName": "Jane Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "TEACHER"
    },
    "studentCount": 50,
    "createdAt": "2025-07-01T09:00:00Z",
    "updatedAt": "2025-07-01T09:00:00Z"
  }
  ```
- `CourseStatsVO`
  **Kotlin 定义**
  ```kotlin
  data class CourseStatsVO(
      val id: UUID,
      val name: String,
      val studentCount: Long,
      val teacher: UserVO,
      val createdAt: String
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
    "name": "Introduction to Kotlin",
    "studentCount": 50,
    "teacher": {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "displayName": "Jane Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "TEACHER"
    },
    "createdAt": "2025-07-01T09:00:00Z"
  }
  ```

---

## 5. 作业接口 (`AssignmentController`)
**基础路径**: `/api/assignments`

### 5.1 创建作业
- **Endpoint**: `POST /api/assignments`
- **请求 Body**: `CreateAssignmentDto`
  **Kotlin 定义**
  ```kotlin
  data class CreateAssignmentDto(
      val title: String,
      val description: String?,
      val courseId: UUID,
      val creatorId: UUID,
      val dueDate: LocalDateTime?, // 截止日期
      val questionIds: List<UUID>  // 题目ID列表
  )
  ```
  **JSON 示例**
  ```json
  {
    "title": "Week 1 Assignment",
    "description": "Questions about basic Kotlin syntax.",
    "courseId": "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
    "creatorId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "dueDate": "2025-07-15T23:59:59Z",
    "questionIds": [
      "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
      "e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15"
    ]
  }
  ```
- **响应**: `201 Created` - `AssignmentVO`

### 5.2 获取作业详情
- **Endpoint**: `GET /api/assignments/{id}`
- **路径参数**: `id: UUID`
- **响应**: `200 OK` - `AssignmentVO`

### 5.3 获取作业列表
- **Endpoint**: `GET /api/assignments`
- **查询参数**:
  - `courseId: UUID` (可选)
  - `creatorId: UUID` (可选)
  - `studentId: UUID` (可选)
- **响应**: `200 OK` - `List<AssignmentVO>`

### 5.4 更新作业
- **Endpoint**: `PUT /api/assignments/{id}`
- **路径参数**: `id: UUID`
- **请求 Body**: `UpdateAssignmentDto`
  **Kotlin 定义**
  ```kotlin
  data class UpdateAssignmentDto(
      val title: String?,
      val description: String?,
      val dueDate: LocalDateTime?,
      val questionIds: List<UUID>?
  )
  ```
  **JSON 示例**
  ```json
  {
    "title": "Week 1 Assignment (Updated)",
    "dueDate": "2025-07-16T23:59:59Z"
  }
  ```
- **响应**: `200 OK` - `AssignmentVO`

### 5.5 删除作业
- **Endpoint**: `DELETE /api/assignments/{id}`
- **路径参数**: `id: UUID`
- **响应**: `204 No Content`

### 5.6 提交作业
- **Endpoint**: `POST /api/assignments/submit`
- **请求 Body**: `SubmitAssignmentDto`
  **Kotlin 定义**
  ```kotlin
  data class SubmitAssignmentDto(
      val assignmentId: UUID,
      val studentId: UUID,
      val answers: Map<UUID, String> // Map<questionId, answer_content>
  )
  ```
  **JSON 示例**
  ```json
  {
    "assignmentId": "f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16",
    "studentId": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
    "answers": {
      "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14": "Answer for question 1",
      "e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15": "Answer for question 2"
    }
  }
  ```
- **响应**: `201 Created` - `AssignmentSubmissionVO`

### 5.7 获取作业的提交列表
- **Endpoint**: `GET /api/assignments/{id}/submissions`
- **路径参数**: `id: UUID` (作业ID)
- **响应**: `200 OK` - `List<AssignmentSubmissionVO>`

### 5.8 获取单个提交详情
- **Endpoint**: `GET /api/assignments/submissions/{submissionId}`
- **路径参数**: `submissionId: UUID`
- **响应**: `200 OK` - `AssignmentSubmissionVO`

### 5.9 批改作业
- **Endpoint**: `POST /api/assignments/submissions/{submissionId}/grade`
- **路径参数**: `submissionId: UUID`
- **请求 Body**: `GradeSubmissionDto`
  **Kotlin 定义**
  ```kotlin
  data class GradeSubmissionDto(
      val scores: Map<UUID, Boolean> // Map<questionId, isCorrect>
  )
  ```
  **JSON 示例**
  ```json
  {
    "scores": {
      "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14": true,
      "e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15": false
    }
  }
  ```
- **响应**: `200 OK` - `AssignmentSubmissionVO`

### 5.10 获取学生的提交列表
- **Endpoint**: `GET /api/assignments/student/{studentId}`
- **路径参数**: `studentId: UUID`
- **响应**: `200 OK` - `List<AssignmentSubmissionVO>`

### 5.11 获取作业统计
- **Endpoint**: `GET /api/assignments/{id}/stats`
- **路径参数**: `id: UUID` (作业ID)
- **响应**: `200 OK` - `AssignmentStatsVO`

### 5.12 获取课程的作业统计
- **Endpoint**: `GET /api/assignments/course/{courseId}/stats`
- **路径参数**: `courseId: UUID`
- **响应**: `200 OK` - `CourseAssignmentStatsVO`

### 响应/请求体定义
- `AssignmentVO`
  **Kotlin 定义**
  ```kotlin
  data class AssignmentVO(
      val id: UUID,
      val title: String,
      val description: String?,
      val course: CourseVO,
      val creator: UserVO,
      val questions: List<QuestionVO>,
      val dueDate: LocalDateTime?,
      val createdAt: LocalDateTime?,
      val updatedAt: LocalDateTime?
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16",
    "title": "Week 1 Assignment",
    "description": "Questions about basic Kotlin syntax.",
    "course": {
      "id": "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
      "name": "Introduction to Kotlin",
      "description": "A comprehensive course on Kotlin programming.",
      "coverImageUrl": "https://example.com/course_cover.png",
      "teacher": {
        "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
        "displayName": "Jane Doe",
        "avatarUrl": "https://example.com/avatar.png",
        "role": "TEACHER"
      },
      "studentCount": 50,
      "createdAt": "2025-07-01T09:00:00Z",
      "updatedAt": "2025-07-01T09:00:00Z"
    },
    "creator": {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "displayName": "Jane Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "TEACHER"
    },
    "questions": [
      {
        "id": "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
        "type": "SINGLE_CHOICE",
        "difficulty": "EASY",
        "content": "What is the keyword for a variable in Kotlin?",
        "explanation": "`val` is for read-only variables, `var` is for mutable variables.",
        "knowledgePoints": [
          {
            "id": "g6eebc99-9c0b-4ef8-bb6d-6bb9bd380a17",
            "name": "Variables",
            "description": "Kotlin variable declaration.",
            "subject": "Kotlin Basics"
          }
        ],
        "creator": {
            "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
            "displayName": "Jane Doe",
            "avatarUrl": "https://example.com/avatar.png",
            "role": "TEACHER"
        },
        "createdAt": "2025-07-05T11:00:00Z",
        "updatedAt": "2025-07-05T11:00:00Z"
      }
    ],
    "dueDate": "2025-07-15T23:59:59Z",
    "createdAt": "2025-07-08T14:00:00Z",
    "updatedAt": "2025-07-08T14:00:00Z"
  }
  ```
- `AssignmentSubmissionVO`
  **Kotlin 定义**
  ```kotlin
  data class AssignmentSubmissionVO(
      val id: UUID,
      val assignment: AssignmentBriefVO,
      val student: UserVO,
      val answers: List<SubmissionAnswerVO>,
      val score: BigDecimal?,
      val status: SubmissionStatus, // (PENDING, SUBMITTED, GRADED)
      val submittedAt: LocalDateTime?
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "h7eebc99-9c0b-4ef8-bb6d-6bb9bd380a18",
    "assignment": {
      "id": "f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16",
      "title": "Week 1 Assignment",
      "dueDate": "2025-07-15T23:59:59Z"
    },
    "student": {
      "id": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
      "displayName": "John Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "STUDENT"
    },
    "answers": [
      {
        "id": "i8eebc99-9c0b-4ef8-bb6d-6bb9bd380a19",
        "question": { "id": "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14", "...": "..." },
        "answer": "Answer for question 1",
        "isCorrect": true
      }
    ],
    "score": 100.0,
    "status": "GRADED",
    "submittedAt": "2025-07-10T10:00:00Z"
  }
  ```
- `AssignmentStatsVO`
  **Kotlin 定义**
  ```kotlin
  data class AssignmentStatsVO(
      val id: UUID,
      val title: String,
      val totalStudents: Long,
      val submissionCount: Long,
      val gradedCount: Long,
      val submissionRate: Double,
      val averageScore: BigDecimal?
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16",
    "title": "Week 1 Assignment",
    "totalStudents": 50,
    "submissionCount": 45,
    "gradedCount": 40,
    "submissionRate": 90.0,
    "averageScore": 85.5
  }
  ```
- `CourseAssignmentStatsVO`
  **Kotlin 定义**
  ```kotlin
  data class CourseAssignmentStatsVO(
      val courseId: UUID,
      val totalAssignments: Int,
      val totalSubmissions: Long,
      val assignments: List<AssignmentStatVO>
  )
  ```
  **JSON 示例**
  ```json
  {
    "courseId": "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
    "totalAssignments": 5,
    "totalSubmissions": 225,
    "assignments": [
      {
        "id": "f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16",
        "title": "Week 1 Assignment",
        "submissionCount": 45,
        "dueDate": "2025-07-15T23:59:59Z"
      }
    ]
  }
  ```

---

## 6. 题目接口 (`QuestionController`)
**基础路径**: `/api/questions`

### 6.1 创建题目
- **Endpoint**: `POST /api/questions`
- **请求 Body**: `CreateQuestionDto`
  **Kotlin 定义**
  ```kotlin
  data class CreateQuestionDto(
      val type: QuestionType, // (SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK, SHORT_ANSWER)
      val difficulty: Difficulty, // (EASY, MEDIUM, HARD)
      val content: String,
      val correctAnswer: String?,
      val explanation: String?,
      val knowledgePointIds: Set<UUID>,
      val creatorId: UUID
  )
  ```
  **JSON 示例**
  ```json
  {
    "type": "SINGLE_CHOICE",
    "difficulty": "EASY",
    "content": "What is the keyword for a read-only variable in Kotlin?",
    "correctAnswer": "val",
    "explanation": "`val` is used for immutable variables.",
    "knowledgePointIds": ["g6eebc99-9c0b-4ef8-bb6d-6bb9bd380a17"],
    "creatorId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
  }
  ```
- **响应**: `201 Created` - `QuestionVO`

### 6.2 获取题目详情
- **Endpoint**: `GET /api/questions/{id}`
- **路径参数**: `id: UUID`
- **响应**: `200 OK` - `QuestionVO`

### 6.3 获取题目列表 (分页)
- **Endpoint**: `GET /api/questions`
- **查询参数**:
  - `type: QuestionType` (可选)
  - `difficulty: Difficulty` (可选)
  - `creatorId: UUID` (可选)
  - `knowledgePointId: UUID` (可选)
  - `subject: String` (可选)
  - `page: Int` (可选, 默认 0)
  - `size: Int` (可选, 默认 20)
- **响应**: `200 OK` - `PageVO<QuestionVO>`

### 6.4 搜索题目
- **Endpoint**: `POST /api/questions/search`
- **请求 Body**: `QuestionSearchDto`
  **Kotlin 定义**
  ```kotlin
  data class QuestionSearchDto(
      val type: QuestionType?,
      val difficulty: Difficulty?,
      val knowledgePointIds: List<UUID>?,
      val subject: String?,
      val creatorId: UUID?,
      val keyword: String?
  )
  ```
  **JSON 示例**
  ```json
  {
    "difficulty": "EASY",
    "subject": "Kotlin Basics",
    "keyword": "variable"
  }
  ```
- **响应**: `200 OK` - `List<QuestionVO>`

### 6.5 更新题目
- **Endpoint**: `PUT /api/questions/{id}`
- **路径参数**: `id: UUID`
- **请求 Body**: `UpdateQuestionDto`
  **Kotlin 定义**
  ```kotlin
  data class UpdateQuestionDto(
      val type: QuestionType?,
      val difficulty: Difficulty?,
      val content: String?,
      val correctAnswer: String?,
      val explanation: String?,
      val knowledgePointIds: Set<UUID>?
  )
  ```
  **JSON 示例**
  ```json
  {
    "difficulty": "MEDIUM",
    "content": "What are the main keywords for variables in Kotlin?"
  }
  ```
- **响应**: `200 OK` - `QuestionVO`

### 6.6 删除题目
- **Endpoint**: `DELETE /api/questions/{id}`
- **路径参数**: `id: UUID`
- **响应**: `204 No Content`

### 6.7 获取题目统计
- **Endpoint**: `GET /api/questions/stats`
- **响应**: `200 OK` - `QuestionStatsVO`

### 6.8 获取所有题目类型
- **Endpoint**: `GET /api/questions/types`
- **响应**: `200 OK` - `List<String>`

### 6.9 获取所有难度等级
- **Endpoint**: `GET /api/questions/difficulties`
- **响应**: `200 OK` - `List<String>`

### 响应/请求体定义
- `QuestionVO`
  **Kotlin 定义**
  ```kotlin
  data class QuestionVO(
      val id: UUID,
      val type: QuestionType,
      val difficulty: Difficulty,
      val content: String,
      val explanation: String?,
      val knowledgePoints: Set<KnowledgePointVO>,
      val creator: UserVO?,
      val createdAt: LocalDateTime?,
      val updatedAt: LocalDateTime?
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
    "type": "SINGLE_CHOICE",
    "difficulty": "EASY",
    "content": "What is the keyword for a variable in Kotlin?",
    "explanation": "`val` is for read-only variables, `var` is for mutable variables.",
    "knowledgePoints": [
      {
        "id": "g6eebc99-9c0b-4ef8-bb6d-6bb9bd380a17",
        "name": "Variables",
        "description": "Kotlin variable declaration.",
        "subject": "Kotlin Basics"
      }
    ],
    "creator": {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "displayName": "Jane Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "TEACHER"
    },
    "createdAt": "2025-07-05T11:00:00Z",
    "updatedAt": "2025-07-05T11:00:00Z"
  }
  ```
- `QuestionStatsVO`
  **Kotlin 定义**
  ```kotlin
  data class QuestionStatsVO(
      val total: Int,
      val byType: Map<QuestionType, Int>,
      val byDifficulty: Map<Difficulty, Int>
  )
  ```
  **JSON 示例**
  ```json
  {
    "total": 150,
    "byType": {
      "SINGLE_CHOICE": 100,
      "MULTIPLE_CHOICE": 50
    },
    "byDifficulty": {
      "EASY": 70,
      "MEDIUM": 50,
      "HARD": 30
    }
  }
  ```

---

## 7. 通知接口 (`NotificationController`)
**基础路径**: `/api/notifications`

### 7.1 发送通知
- **Endpoint**: `POST /api/notifications`
- **请求 Body**: `CreateNotificationDto`
  **Kotlin 定义**
  ```kotlin
  data class CreateNotificationDto(
      val title: String,
      val content: String,
      val type: NotificationType, // (SYSTEM, ASSIGNMENT, GRADE, MESSAGE)
      val recipientId: UUID,
      val senderId: UUID?
  )
  ```
  **JSON 示例**
  ```json
  {
    "title": "New Assignment Published",
    "content": "A new assignment has been published for 'Introduction to Kotlin'.",
    "type": "ASSIGNMENT",
    "recipientId": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
    "senderId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
  }
  ```
- **响应**: `201 Created` - `NotificationVO`

### 7.2 广播通知
- **Endpoint**: `POST /api/notifications/broadcast`
- **请求 Body**: `BroadcastNotificationDto`
  **Kotlin 定义**
  ```kotlin
  data class BroadcastNotificationDto(
      val title: String,
      val content: String,
      val role: String, // e.g., "STUDENT", "TEACHER"
      val type: NotificationType,
      val senderId: UUID?
  )
  ```
  **JSON 示例**
  ```json
  {
    "title": "System Maintenance",
    "content": "The system will be down for maintenance tonight.",
    "role": "STUDENT",
    "type": "SYSTEM",
    "senderId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
  }
  ```
- **响应**: `201 Created` - `List<NotificationVO>`

### 7.3 获取用户通知
- **Endpoint**: `GET /api/notifications/user/{userId}`
- **路径参数**: `userId: UUID`
- **查询参数**:
  - `unreadOnly: Boolean` (可选, 默认 `false`)
  - `type: NotificationType` (可选)
- **响应**: `200 OK` - `List<NotificationVO>`

### 7.4 获取用户通知摘要
- **Endpoint**: `GET /api/notifications/user/{userId}/summary`
- **路径参数**: `userId: UUID`
- **响应**: `200 OK` - `NotificationSummaryVO`

### 7.5 标记为已读
- **Endpoint**: `PUT /api/notifications/{id}/read`
- **路径参数**: `id: UUID` (通知ID)
- **查询参数**: `userId: UUID` (必需)
- **响应**: `200 OK` - `NotificationVO`

### 7.6 全部标记为已读
- **Endpoint**: `PUT /api/notifications/user/{userId}/read-all`
- **路径参数**: `userId: UUID`
- **响应**: `200 OK` - `MessageVO`

### 7.7 批量标记为已读
- **Endpoint**: `POST /api/notifications/mark-read`
- **请求 Body**: `MarkReadDto`
  **Kotlin 定义**
  ```kotlin
  data class MarkReadDto(
      val notificationIds: List<UUID>,
      val userId: UUID
  )
  ```
  **JSON 示例**
  ```json
  {
    "notificationIds": [
      "j9eebc99-9c0b-4ef8-bb6d-6bb9bd380a20",
      "k0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21"
    ],
    "userId": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12"
  }
  ```
- **响应**: `200 OK` - `MessageVO`

### 7.8 删除通知
- **Endpoint**: `DELETE /api/notifications/{id}`
- **路径参数**: `id: UUID`
- **查询参数**: `userId: UUID` (必需)
- **响应**: `204 No Content`

### 7.9 获取通知类型
- **Endpoint**: `GET /api/notifications/types`
- **响应**: `200 OK` - `List<String>`

### 7.10 清理旧通知
- **Endpoint**: `DELETE /api/notifications/cleanup`
- **查询参数**: `daysOld: Int` (可选, 默认 30)
- **响应**: `200 OK` - `MessageVO`

### 7.11 获取通知统计
- **Endpoint**: `GET /api/notifications/stats`
- **响应**: `200 OK` - `NotificationStatsVO`

### 响应/请求体定义
- `NotificationVO`
  **Kotlin 定义**
  ```kotlin
  data class NotificationVO(
      val id: UUID,
      val title: String,
      val content: String,
      val type: NotificationType,
      val sender: UserVO?,
      val recipient: UserVO,
      val isRead: Boolean,
      val readAt: LocalDateTime?,
      val createdAt: LocalDateTime?
  )
  ```
  **JSON 示例**
  ```json
  {
    "id": "j9eebc99-9c0b-4ef8-bb6d-6bb9bd380a20",
    "title": "New Assignment Published",
    "content": "A new assignment has been published for 'Introduction to Kotlin'.",
    "type": "ASSIGNMENT",
    "sender": {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "displayName": "Jane Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "TEACHER"
    },
    "recipient": {
      "id": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
      "displayName": "John Doe",
      "avatarUrl": "https://example.com/avatar.png",
      "role": "STUDENT"
    },
    "isRead": false,
    "readAt": null,
    "createdAt": "2025-07-08T15:00:00Z"
  }
  ```
- `NotificationSummaryVO`
  **Kotlin 定义**
  ```kotlin
  data class NotificationSummaryVO(
      val totalCount: Long,
      val unreadCount: Long,
      val byType: Map<NotificationType, Long>
  )
  ```
  **JSON 示例**
  ```json
  {
    "totalCount": 10,
    "unreadCount": 3,
    "byType": {
      "ASSIGNMENT": 5,
      "GRADE": 2,
      "SYSTEM": 3
    }
  }
  ```
- `NotificationStatsVO`
  **Kotlin 定义**
  ```kotlin
  data class NotificationStatsVO(
      val total: Int,
      val byType: Map<NotificationType, Int>,
      val readStatus: Map<String, Int> // e.g., {"read": 10, "unread": 5}
  )
  ```
  **JSON 示例**
  ```json
  {
    "total": 100,
    "byType": {
      "ASSIGNMENT": 50,
      "GRADE": 20,
      "SYSTEM": 30
    },
    "readStatus": {
      "read": 80,
      "unread": 20
    }
  }
  ```
---

## 8. 存储接口 (`StorageController`)
**基础路径**: `/api/storage`

### 8.1 上传文件
- **描述**: 上传一个文件到对象存储服务(OSS)
- **Endpoint**: `POST /api/storage/upload`
- **认证**: 需要
- **请求参数**:
  - **类型**: `multipart/form-data`
  - **参数**:
    - `file`: `MultipartFile` (必需) - 要上传的文件
    - `uploaderId`: `UUID` (必需) - 上传者的用户ID
- **响应**: `200 OK`
  - **Body**: `StorageObjectVO`

### 8.2 获取文件详情
- **描述**: 根据文件ID获取文件的详细信息
- **Endpoint**: `GET /api/storage/{id}`
- **认证**: 需要
- **请求参数**:
  - **路径参数**:
    - `id`: `UUID` (必需) - 文件ID
- **响应**:
  - `200 OK`: `StorageObjectVO`
  - `404 Not Found`: 如果文件不存在

### 8.3 请求文件向量化
- **描述**: 异步请求对已上传的文件进行向量化处理
- **Endpoint**: `POST /api/storage/{id}/embed`
- **认证**: 需要
- **请求参数**:
  - **路径参数**:
    - `id`: `UUID` (必需) - 文件ID
- **响应**: `202 Accepted`
  - **Body**: `StatusVO` (告知请求已被接受并排队处理)

### 8.4 获取文件列表 (分页)
- **描述**: 根据条件分页获取文件列表。普通用户默认只能看到自己上传的文件，管理员可以看到所有文件
- **Endpoint**: `GET /api/storage`
- **认证**: 需要
- **查询参数**:
  - `currentUserId`: `UUID` (必需) - 当前操作用户的ID。**注意**: 这是一个临时的身份验证方案，在未来的版本中将被替换为基于令牌的安全上下文
  - `uploaderId`: `UUID` (可选) - 按上传者ID筛选。仅管理员可用
  - `keyword`: `String` (可选) - 按文件名进行模糊搜索
  - `page`: `Int` (可选, 默认 `0`) - 页码
  - `size`: `Int` (可选, 默认 `20`) - 每页大小
- **响应**: `200 OK`
  - **Body**: `PageVO<StorageObjectVO>`

### `StorageObjectVO`
**Kotlin 定义**
```kotlin
data class StorageObjectVO(
    val id: UUID,
    val objectKey: String,
    val originalFilename: String,
    val fileSize: Long,
    val contentType: String?,
    val storageProvider: String,
    val bucketName: String,
    val embeddingStatus: EmbeddingStatus, // (NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED)
    val uploader: UserVO?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
```
**JSON 示例**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "objectKey": "uploads/myfile.pdf",
  "originalFilename": "myfile.pdf",
  "fileSize": 102400,
  "contentType": "application/pdf",
  "storageProvider": "minio",
  "bucketName": "janus",
  "embeddingStatus": "COMPLETED",
  "uploader": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "displayName": "Jane Doe",
    "avatarUrl": "https://example.com/avatar.png",
    "role": "TEACHER"
  },
  "createdAt": "2025-07-05T10:00:00Z",
  "updatedAt": "2025-07-05T10:05:00Z"
}
```

---

## 9. 资源接口 (`ResourceController`)
**基础路径**: `/api/resources`

### 9.1 获取资源列表
- **描述**: 获取资源列表，以用户友好的方式展示存储对象，适用于前端资源管理页面
- **Endpoint**: `GET /api/resources`
- **认证**: 需要
- **查询参数**:
  - `currentUserId`: `UUID` (必需) - 当前用户ID
  - `page`: `Int` (可选, 默认 `0`) - 页码
  - `size`: `Int` (可选, 默认 `10`) - 每页大小
- **响应**: `200 OK`
  - **Body**: `List<ResourceVO>`

### `ResourceVO`
**Kotlin 定义**
```kotlin
data class ResourceVO(
    val id: UUID,
    val title: String,
    val type: String,
    val subject: String?,
    val uploader: String,
    val uploadDate: String,
    val fileSize: Long,
    val downloadUrl: String?
)
```
**JSON 示例**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Kotlin Programming Guide",
  "type": "PDF",
  "subject": "Programming",
  "uploader": "Jane Doe",
  "uploadDate": "2025-07-05T10:00:00Z",
  "fileSize": 102400,
  "downloadUrl": "https://example.com/download/myfile.pdf"
}
```

---

## 10. AI 接口 (`EmbeddingController`)
**基础路径**: `/ai`

### 10.1 文本向量化并存储
- **描述**: 将给定的文本进行向量化并存储到向量数据库
- **Endpoint**: `POST /ai/embedding/store`
- **认证**: 需要
- **请求**:
  - **Body**: `EmbeddingRequest`
    **Kotlin 定义**
    ```kotlin
    data class EmbeddingRequest(
        val message: String // 需要向量化的文本
    )
    ```
    **JSON 示例**
    ```json
    {
      "message": "This is a text to be embedded."
    }
    ```
- **响应**: `200 OK`
  - **Body**: `StatusVO` (告知操作成功)

### 10.2 文本向量化
- **描述**: 对给定的文本进行向量化，直接返回向量结果
- **Endpoint**: `POST /ai/embedding`
- **认证**: 需要
- **请求**:
  - **Body**: `EmbeddingRequest`
- **响应**: `200 OK`
  - **Body**: `EmbeddingVO`
    **Kotlin 定义**
    ```kotlin
    data class EmbeddingVO(
        // Spring AI 的 EmbeddingResponse，通常包含一个浮点数数组
        val embedding: EmbeddingResponse
    )
    ```
    **JSON 示例**
    ```json
    {
      "embedding": {
        "embedding": [0.1, 0.2, 0.3],
        "metadata": {}
      }
    }
    ```

---

## 11. 统计接口 (`StatsController`)
**基础路径**: `/api/stats`

### 11.1 获取性能统计
- **描述**: 获取平台整体的性能统计数据，例如平均正确率和常见错误概念
- **Endpoint**: `GET /api/stats`
- **认证**: 需要
- **请求参数**:
  - **查询参数**:
    - `userId`: `UUID` (可选) - 用户ID，用于获取特定用户的统计数据
- **响应**: `200 OK`
  - **Body**: `PerformanceStatsVO`
    **Kotlin 定义**
    ```kotlin
    data class PerformanceStatsVO(
        val averageAccuracy: Double,         // 平均正确率
        val frequentlyMissedConcepts: List<String> // 常见错误概念列表
    )
    ```
    **JSON 示例**
    ```json
    {
      "averageAccuracy": 85.0,
      "frequentlyMissedConcepts": ["数据结构", "算法"]
    }
    ```

---

## 12. 分析接口 (`AnalysisController`)
**基础路径**: `/api/analysis`

### 12.1 获取学生分析
- **描述**: 获取学生学习分析数据，包括错误题目分析和改进建议。**注意**: 这是一个模拟实现，返回硬编码的分析结果
- **Endpoint**: `GET /api/analysis`
- **认证**: 需要
- **请求参数**:
  - **查询参数**:
    - `userId`: `UUID` (可选) - 用户ID
- **响应**: `200 OK`
  - **Body**: `List<StudentAnalysisVO>`
    **Kotlin 定义**
    ```kotlin
    data class StudentAnalysisVO(
        val id: UUID,
        val studentId: UUID,
        val studentName: String,
        val incorrectQuestions: String,
        val errorLocation: String,
        val suggestedCorrection: String
    )
    ```
    **JSON 示例**
    ```json
    [
      {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "studentId": "456e7890-e89b-12d3-a456-426614174001",
        "studentName": "Ethan Harper",
        "incorrectQuestions": "Question 5, Question 8",
        "errorLocation": "Step 2, Step 4",
        "suggestedCorrection": "Review algebraic equations"
      }
    ]
    ```

---

## 13. 教学大纲接口 (`SyllabusController`)
**基础路径**: `/api/syllabus`

### 13.1 获取教学大纲
- **描述**: 获取教学大纲信息。**注意**: 这是一个简化实现，获取数据库中第一个可用的课程计划
- **Endpoint**: `GET /api/syllabus`
- **认证**: 需要
- **请求参数**: 无
- **响应**:
  - `200 OK`: `SyllabusVO`
  - `404 Not Found`: 如果没有找到课程计划

### `SyllabusVO`
**Kotlin 定义**
```kotlin
data class SyllabusVO(
    val id: UUID,
    val courseId: UUID?,
    val name: String,
    val chapters: List<ChapterVO>,
    val isGenerating: Boolean
)
```
**JSON 示例**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "courseId": "456e7890-e89b-12d3-a456-426614174001",
  "name": "Kotlin Programming Syllabus",
  "chapters": [
    {
      "id": "789e0123-e89b-12d3-a456-426614174002",
      "title": "Introduction to Kotlin",
      "content": "Basic concepts and syntax",
      "order": 1
    }
  ],
  "isGenerating": false
}
```

---

## 14. 菜单接口 (`MenuController`)
**基础路径**: `/api/menu`

### 14.1 获取教师菜单
- **描述**: 获取教师角色的导航菜单项
- **Endpoint**: `GET /api/menu/teacher`
- **认证**: 需要
- **请求参数**: 无
- **响应**: `200 OK`
  - **Body**: `List<MenuItemVO>`

### 14.2 获取学生菜单
- **描述**: 获取学生角色的导航菜单项
- **Endpoint**: `GET /api/menu/student`
- **认证**: 需要
- **请求参数**: 无
- **响应**: `200 OK`
  - **Body**: `List<MenuItemVO>`

### 14.3 获取管理员菜单
- **描述**: 获取管理员角色的导航菜单项
- **Endpoint**: `GET /api/menu/admin`
- **认证**: 需要
- **请求参数**: 无
- **响应**: `200 OK`
  - **Body**: `List<MenuItemVO>`

### `MenuItemVO`
**Kotlin 定义**
```kotlin
data class MenuItemVO(
    val id: String,
    val label: String,
    val icon: String,
    val path: String,
    val isActive: Boolean = false
)
```
**JSON 示例**
```json
[
  {
    "id": "1",
    "label": "主页",
    "icon": "home",
    "path": "/home",
    "isActive": true
  },
  {
    "id": "2",
    "label": "课程准备",
    "icon": "book",
    "path": "/syllabus",
    "isActive": false
  }
]
```

---

## 错误处理

所有控制器都包含统一的错误处理机制：

### 通用错误响应
- **400 Bad Request**: 请求参数无效
  ```json
  {
    "error": "Bad Request",
    "message": "Invalid request parameters"
  }
  ```

- **404 Not Found**: 资源不存在
  ```json
  {
    "error": "Not Found",
    "message": "Resource not found"
  }
  ```

- **500 Internal Server Error**: 服务器内部错误
  ```json
  {
    "error": "Internal Server Error",
    "message": "An unexpected error occurred"
  }
  ```

---

## 认证和授权

**注意**: 当前实现使用临时的身份验证方案（通过查询参数传递用户ID）。在生产环境中，应该实现基于令牌的身份验证系统（如JWT）和适当的授权机制。

---

## 版本信息

- **API 版本**: v1
- **最后更新**: 2025-07-26
- **兼容性**: 向后兼容

---

## 联系信息

如有问题或建议，请联系开发团队。
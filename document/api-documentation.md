# API 接口文档

## 概述

本文档描述了 Janus Eye 教学平台的 API 接口规范。所有 API 接口均基于 RESTful 架构设计，使用 JSON 格式进行数据交互。

**基础 URL**: `http://localhost:3001/api`

## 接口列表

### 1. 用户管理 (User Management)

#### 1.1 获取所有用户
- **接口地址**: `GET /users`
- **接口描述**: 获取系统中所有用户信息
- **请求参数**: 无
- **响应格式**: 
  ```json
  [
    {
      "id": "string",
      "name": "string",
      "email": "string",
      "avatar": "string",
      "role": "teacher" | "student" | "admin"
    }
  ]
  ```

### 2. 课程管理 (Course Management)

#### 2.1 获取所有课程
- **接口地址**: `GET /courses`
- **接口描述**: 获取系统中所有课程信息
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "name": "string",
      "description": "string",
      "teacher": "string",
      "students": "number",
      "progress": "number",
      "image": "string" (可选)
    }
  ]
  ```

### 3. 题目管理 (Question Management)

#### 3.1 获取所有题目
- **接口地址**: `GET /questions`
- **接口描述**: 获取系统中所有题目信息
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "title": "string",
      "type": "multiple-choice" | "true-false" | "short-answer" | "essay",
      "difficulty": "easy" | "medium" | "hard",
      "knowledgePoints": ["string"],
      "options": ["string"] (可选),
      "correctAnswer": "string | number" (可选),
      "explanation": "string" (可选)
    }
  ]
  ```

### 4. 作业管理 (Assignment Management)

#### 4.1 获取所有作业
- **接口地址**: `GET /assignments`
- **接口描述**: 获取系统中所有作业信息
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "title": "string",
      "description": "string",
      "dueDate": "string",
      "courseId": "string",
      "questions": [Question对象],
      "submissions": "number",
      "maxScore": "number"
    }
  ]
  ```

### 5. 通知管理 (Notification Management)

#### 5.1 获取所有通知
- **接口地址**: `GET /notifications`
- **接口描述**: 获取系统中所有通知信息
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "title": "string",
      "content": "string",
      "type": "info" | "warning" | "success" | "error",
      "isRead": "boolean",
      "createdAt": "string",
      "avatar": "string" (可选)
    }
  ]
  ```

### 6. 统计数据 (Statistics)

#### 6.1 获取性能统计数据
- **接口地址**: `GET /stats`
- **接口描述**: 获取学生学习性能统计数据
- **请求参数**: 无
- **响应格式**:
  ```json
  {
    "averageAccuracy": "number",
    "frequentlyMissedConcepts": ["string"],
    "classRanking": "string",
    "knowledgePointMastery": {
      "知识点名称": "number"
    },
    "accuracyTrends": [
      {
        "week": "string",
        "accuracy": "number"
      }
    ],
    "questionTypeDistribution": [
      {
        "type": "string",
        "percentage": "number"
      }
    ]
  }
  ```

### 7. 教学大纲 (Syllabus)

#### 7.1 获取教学大纲
- **接口地址**: `GET /syllabus`
- **接口描述**: 获取课程教学大纲信息
- **请求参数**: 无
- **响应格式**:
  ```json
  {
    "id": "string",
    "courseId": "string",
    "chapters": [
      {
        "id": "string",
        "title": "string",
        "order": "number",
        "content": "string",
        "exercises": [Question对象],
        "isCompleted": "boolean"
      }
    ],
    "isGenerating": "boolean",
    "progress": "number"
  }
  ```

### 8. 学生分析 (Student Analysis)

#### 8.1 获取学生分析数据
- **接口地址**: `GET /analysis`
- **接口描述**: 获取学生学习分析数据
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "studentName": "string",
      "incorrectQuestions": "string",
      "errorLocation": "string",
      "suggestedCorrection": "string"
    }
  ]
  ```

### 9. 导航菜单 (Navigation Menu)

#### 9.1 获取教师导航菜单
- **接口地址**: `GET /menu/teacher`
- **接口描述**: 获取教师角色的导航菜单
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "label": "string",
      "icon": "string",
      "path": "string",
      "isActive": "boolean" (可选)
    }
  ]
  ```

#### 9.2 获取学生导航菜单
- **接口地址**: `GET /menu/student`
- **接口描述**: 获取学生角色的导航菜单
- **请求参数**: 无
- **响应格式**: 同上

#### 9.3 获取管理员导航菜单
- **接口地址**: `GET /menu/admin`
- **接口描述**: 获取管理员角色的导航菜单
- **请求参数**: 无
- **响应格式**: 同上

### 10. 资源管理 (Resource Management)

#### 10.1 获取所有资源
- **接口地址**: `GET /resources`
- **接口描述**: 获取系统中所有学习资源
- **请求参数**: 无
- **响应格式**:
  ```json
  [
    {
      "id": "string",
      "title": "string",
      "type": "video" | "document" | "image" | "assessment",
      "subject": "string",
      "uploader": "string",
      "uploadDate": "string",
      "thumbnail": "string" (可选)
    }
  ]
  ```

## 错误处理

所有接口在发生错误时都会返回相应的 HTTP 状态码和错误信息：

- **200**: 请求成功
- **400**: 请求参数错误
- **401**: 未授权访问
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

错误响应格式：
```json
{
  "error": "错误类型",
  "message": "错误详细信息"
}
```

## 注意事项

1. 所有接口均使用 JSON 格式进行数据交互
2. 日期时间格式使用 ISO 8601 标准
3. 所有字符串字段均使用 UTF-8 编码
4. 接口返回的数据均为测试数据，实际使用时需要连接真实数据库

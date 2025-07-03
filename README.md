# 文档说明

## 文档概述

本目录包含了 Janus Eye 教学平台的完整技术文档，为开发者提供了详细的接口规范和数据结构定义。

## 文档结构

### 1. [API 接口文档](./api-documentation.md)
- **描述**: 详细说明了所有 API 接口的调用方式、参数格式和返回数据结构
- **适用人群**: 前端开发者、后端开发者、测试人员
- **内容包括**:
  - 10 个主要功能模块的接口定义
  - 请求参数和响应格式说明
  - 错误处理机制
  - 使用示例

### 2. [数据结构定义文档](./data-structures.md)
- **描述**: 定义了系统中所有数据结构和类型定义
- **适用人群**: 前端开发者、后端开发者
- **内容包括**:
  - 10 个核心数据结构的详细定义
  - 字段说明和类型约束
  - 使用规范和扩展说明

### 3. [产品需求文档](./prd.md)
- **描述**: 产品功能需求和设计规范
- **适用人群**: 产品经理、设计师、开发者

### 4. [开发步骤文档](./step1.md)
- **描述**: 项目开发的步骤和流程说明
- **适用人群**: 开发者、项目经理

## 快速开始

### 1. 查看 API 接口
如果您需要了解如何调用系统接口，请查看 [API 接口文档](./api-documentation.md)。

### 2. 了解数据结构
如果您需要了解系统中的数据类型定义，请查看 [数据结构定义文档](./data-structures.md)。

### 3. 接口使用示例
```typescript
// 导入类型定义
import type { User, Course, Question } from '@/types'

// 导入 API 函数
import { getUsers, getCourses, getQuestions } from '@/api'

// 使用示例
const users: User[] = await getUsers()
const courses: Course[] = await getCourses()
const questions: Question[] = await getQuestions()
```

## 接口概览

| 功能模块 | 接口地址 | 方法 | 描述 |
|---------|----------|------|------|
| 用户管理 | `/api/users` | GET | 获取所有用户 |
| 课程管理 | `/api/courses` | GET | 获取所有课程 |
| 题目管理 | `/api/questions` | GET | 获取所有题目 |
| 作业管理 | `/api/assignments` | GET | 获取所有作业 |
| 通知管理 | `/api/notifications` | GET | 获取所有通知 |
| 统计数据 | `/api/stats` | GET | 获取性能统计 |
| 教学大纲 | `/api/syllabus` | GET | 获取教学大纲 |
| 学生分析 | `/api/analysis` | GET | 获取学生分析 |
| 导航菜单 | `/api/menu/{role}` | GET | 获取角色菜单 |
| 资源管理 | `/api/resources` | GET | 获取所有资源 |

## 数据结构概览

| 数据结构 | 描述 | 主要字段 |
|---------|------|----------|
| User | 用户信息 | id, name, email, avatar, role |
| Course | 课程信息 | id, name, description, teacher, students |
| Question | 题目信息 | id, title, type, difficulty, knowledgePoints |
| Assignment | 作业信息 | id, title, description, dueDate, questions |
| Notification | 通知信息 | id, title, content, type, isRead |
| PerformanceStats | 性能统计 | averageAccuracy, frequentlyMissedConcepts |
| Syllabus | 教学大纲 | id, courseId, chapters, isGenerating |
| StudentAnalysis | 学生分析 | id, studentName, incorrectQuestions |
| MenuItem | 菜单项 | id, label, icon, path, isActive |
| Resource | 资源信息 | id, title, type, subject, uploader |

## 开发环境配置

### 1. 前端开发
```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

### 2. 后端服务
```bash
# 启动模拟服务器
node server/index.cjs
```

### 3. 访问地址
- 前端应用: `http://localhost:5173`
- API 服务: `http://localhost:3001`

## 注意事项

1. **测试数据**: 当前所有接口返回的都是测试数据，实际部署时需要连接真实数据库
2. **类型安全**: 建议使用 TypeScript 进行开发，以确保类型安全
3. **错误处理**: 所有 API 调用都应该包含适当的错误处理逻辑
4. **数据格式**: 日期时间统一使用 ISO 8601 格式
5. **编码格式**: 所有字符串均使用 UTF-8 编码

## 文档更新

在修改代码时，请确保同步更新相关文档：

1. 新增接口时，更新 API 接口文档
2. 修改数据结构时，更新数据结构定义文档
3. 重要变更时，更新此 README 文档

## 联系方式

如有问题或建议，请联系开发团队。

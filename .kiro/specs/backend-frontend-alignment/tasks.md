# 后端前端数据结构对齐实施任务

## 实施计划

- [x] 1. 创建基础响应包装器和异常处理





  - 实现统一的API响应格式，包括成功和错误响应的包装器
  - 创建全局异常处理器，确保所有错误都返回统一格式
  - _需求: 1.1, 1.2, 1.3_

- [x] 1.1 实现ApiResponse和ApiErrorResponse类


  - 创建 `ApiResponse<T>` 泛型类用于成功响应包装
  - 创建 `ApiErrorResponse` 类用于错误响应包装
  - 创建 `PageVO<T>` 类用于分页数据包装
  - _需求: 1.1, 8.1, 8.2_

- [x] 1.2 实现全局异常处理器


  - 创建 `GlobalExceptionHandler` 类处理所有异常
  - 实现标准化的错误代码和错误消息
  - 确保所有异常都返回 `ApiErrorResponse` 格式
  - _需求: 1.3_

- [x] 1.3 创建枚举转换器


  - 实现 `EnumConverter` 类处理数据库枚举值到前端枚举值的转换
  - 支持用户角色、题目类型、难度等级等枚举的转换
  - 确保所有枚举值都转换为大写格式
  - _需求: 9.1, 9.2_

- [ ] 2. 实现用户相关VO映射器
  - 创建UserVOMapper将User实体映射为UserVO
  - 确保字段名称对齐（displayName, avatarUrl, 大写角色）
  - _需求: 2.1, 2.2, 2.3_

- [ ] 2.1 创建UserVO数据类
  - 定义符合前端规范的 `UserVO` 数据类
  - 包含 `id`, `displayName`, `email`, `avatarUrl`, `role` 字段
  - 确保所有字段类型与前端期望一致
  - _需求: 2.1, 2.2, 2.3_

- [ ] 2.2 实现UserVOMapper
  - 创建 `UserVOMapper` 组件类
  - 实现 `toVO(user: User): UserVO` 方法
  - 处理 `displayName` 字段的映射逻辑（优先使用displayName，否则使用username）
  - 确保角色枚举值转换为大写格式
  - _需求: 2.1, 2.2, 2.3_

- [ ] 2.3 更新UserController使用新的响应格式
  - 修改所有用户相关的API端点返回 `ApiResponse<UserVO>` 格式
  - 集成UserVOMapper进行实体到VO的转换
  - 更新分页查询返回 `ApiResponse<PageVO<UserVO>>` 格式
  - _需求: 1.1, 2.1, 8.1_

- [ ] 3. 实现课程相关VO映射器
  - 创建CourseVOMapper将Course实体映射为CourseVO
  - 确保teacher字段返回完整UserVO对象，coverImageUrl字段对齐
  - _需求: 3.1, 3.2, 3.3_

- [ ] 3.1 创建CourseVO数据类
  - 定义符合前端规范的 `CourseVO` 数据类
  - 包含 `id`, `name`, `description`, `teacher`, `coverImageUrl` 字段
  - 确保 `teacher` 字段类型为 `UserVO` 而非字符串
  - _需求: 3.1, 3.2_

- [ ] 3.2 实现CourseVOMapper
  - 创建 `CourseVOMapper` 组件类，依赖 `UserVOMapper`
  - 实现 `toVO(course: Course): CourseVO` 方法
  - 确保teacher字段通过UserVOMapper转换为完整的UserVO对象
  - 处理coverImageUrl字段的映射
  - _需求: 3.1, 3.2_

- [ ] 3.3 创建CourseEnrollmentVO和相关API
  - 创建 `CourseEnrollmentVO` 数据类
  - 实现独立的选课API端点 `/api/courses/{id}/enrollments`
  - 移除Course中的students数量字段，通过独立API获取
  - _需求: 3.3_

- [ ] 3.4 更新CourseController使用新的响应格式
  - 修改所有课程相关的API端点返回统一响应格式
  - 集成CourseVOMapper进行实体到VO的转换
  - 实现课程选课相关的独立API端点
  - _需求: 1.1, 3.1, 3.3_

- [ ] 4. 实现题目相关VO映射器
  - 创建QuestionVOMapper处理题目实体到VO的复杂映射
  - 处理content字段、选项格式转换、枚举值对齐
  - _需求: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 4.1 创建QuestionVO数据类
  - 定义符合前端规范的 `QuestionVO` 数据类
  - 包含 `id`, `content`, `type`, `difficulty`, `knowledgePointIds`, `options`, `correctAnswer`, `explanation` 字段
  - 确保 `options` 字段类型为 `Map<String, String>?`
  - _需求: 4.1, 4.4, 4.5_

- [ ] 4.2 实现QuestionVOMapper
  - 创建 `QuestionVOMapper` 组件类
  - 实现题目类型枚举值的映射（MULTIPLE_CHOICE → SINGLE_CHOICE等）
  - 实现难度枚举值的大写转换
  - 解析JSON格式的content字段，提取题干和选项
  - 将knowledgePoints关联转换为knowledgePointIds数组
  - _需求: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 4.3 创建KnowledgePointVO数据类
  - 定义符合前端规范的 `KnowledgePointVO` 数据类
  - 包含 `id`, `name`, `description`, `subject`, `parentId` 字段
  - 实现KnowledgePointVOMapper
  - _需求: 4.4_

- [ ] 4.4 更新QuestionController使用新的响应格式
  - 修改所有题目相关的API端点返回统一响应格式
  - 集成QuestionVOMapper进行实体到VO的转换
  - 更新分页查询和搜索功能
  - _需求: 1.1, 4.1, 8.1_

- [ ] 5. 实现作业相关VO映射器
  - 创建AssignmentVOMapper处理作业实体映射
  - 确保questionIds数组格式、状态枚举值对齐
  - _需求: 5.1, 5.2, 5.3_

- [ ] 5.1 创建AssignmentVO和相关数据类
  - 定义符合前端规范的 `AssignmentVO` 数据类
  - 创建 `AssignmentSubmissionVO` 和 `SubmissionAnswerVO` 数据类
  - 确保 `questionIds` 字段为字符串数组，`courseId` 为字符串
  - 确保状态枚举值为大写格式
  - _需求: 5.1, 5.2, 5.3_

- [ ] 5.2 实现AssignmentVOMapper
  - 创建 `AssignmentVOMapper` 组件类
  - 实现questions关联转换为questionIds数组
  - 实现course关联转换为courseId字符串
  - 处理提交状态枚举值的大写转换
  - 实现日期字段的ISO 8601格式转换
  - _需求: 5.1, 5.2, 5.3, 10.1_

- [ ] 5.3 更新AssignmentController使用新的响应格式
  - 修改所有作业相关的API端点返回统一响应格式
  - 集成AssignmentVOMapper进行实体到VO的转换
  - 实现作业提交相关的API端点
  - _需求: 1.1, 5.1, 5.2_

- [ ] 6. 实现存储对象VO映射器
  - 创建StorageObjectVOMapper处理存储对象映射
  - 确保url字段、uploaderId字段、枚举值对齐
  - _需求: 6.1, 6.2, 6.3_

- [ ] 6.1 创建StorageObjectVO数据类
  - 定义符合前端规范的 `StorageObjectVO` 数据类
  - 包含 `id`, `objectKey`, `url`, `originalFilename`, `contentType`, `fileSize`, `uploaderId`, `createdAt` 字段
  - 确保 `uploaderId` 字段为字符串类型
  - _需求: 6.1, 6.2_

- [ ] 6.2 实现StorageObjectVOMapper
  - 创建 `StorageObjectVOMapper` 组件类
  - 实现uploader关联转换为uploaderId字符串
  - 生成文件的可访问URL
  - 处理嵌入状态枚举值的大写转换
  - 实现日期字段的ISO 8601格式转换
  - _需求: 6.1, 6.2, 6.3, 10.1_

- [ ] 6.3 更新StorageController使用新的响应格式
  - 修改所有存储相关的API端点返回统一响应格式
  - 集成StorageObjectVOMapper进行实体到VO的转换
  - 更新文件上传和列表查询功能
  - _需求: 1.1, 6.1, 6.2_

- [ ] 7. 实现通知相关VO映射器
  - 创建NotificationVOMapper处理通知实体映射
  - 确保类型枚举值、senderId字段对齐
  - _需求: 7.1, 7.2, 7.3_

- [ ] 7.1 创建NotificationVO数据类
  - 定义符合前端规范的 `NotificationVO` 数据类
  - 包含 `id`, `title`, `content`, `type`, `isRead`, `createdAt`, `senderId` 字段
  - 确保 `senderId` 字段为可选的字符串类型
  - _需求: 7.1, 7.2_

- [ ] 7.2 实现NotificationVOMapper
  - 创建 `NotificationVOMapper` 组件类
  - 实现通知类型枚举值的映射和大写转换
  - 实现sender关联转换为senderId字符串
  - 实现日期字段的ISO 8601格式转换
  - _需求: 7.1, 7.2, 7.3, 10.1_

- [ ] 7.3 更新NotificationController使用新的响应格式
  - 修改所有通知相关的API端点返回统一响应格式
  - 集成NotificationVOMapper进行实体到VO的转换
  - 更新通知查询和管理功能
  - _需求: 1.1, 7.1, 7.2_

- [ ] 8. 创建数据库迁移脚本
  - 创建Flyway迁移脚本更新数据库中的枚举值
  - 确保数据一致性和向后兼容性
  - _需求: 9.3_

- [ ] 8.1 分析现有数据库枚举值
  - 检查所有表中的枚举字段当前值
  - 识别需要更新的枚举值和约束
  - 创建数据备份策略
  - _需求: 9.3_

- [ ] 8.2 创建枚举值更新迁移脚本
  - 创建新的Flyway迁移文件更新用户角色枚举值
  - 创建迁移文件更新题目类型和难度枚举值
  - 创建迁移文件更新提交状态枚举值
  - 更新相关的CHECK约束
  - _需求: 9.1, 9.3_

- [ ] 8.3 验证数据迁移
  - 在测试环境执行迁移脚本
  - 验证所有枚举值都正确更新
  - 确保应用程序能正常读取更新后的数据
  - _需求: 9.3_

- [ ] 9. 编写单元测试
  - 为所有VO映射器编写单元测试
  - 测试枚举转换、字段映射、异常处理
  - _需求: 所有需求的测试覆盖_

- [ ] 9.1 编写VO映射器单元测试
  - 为UserVOMapper编写测试，验证字段映射和角色转换
  - 为CourseVOMapper编写测试，验证teacher对象映射
  - 为QuestionVOMapper编写测试，验证复杂的content解析和选项转换
  - 为AssignmentVOMapper编写测试，验证关联字段转换
  - _需求: 2.1, 3.1, 4.1, 5.1_

- [ ] 9.2 编写响应包装器单元测试
  - 测试ApiResponse的正确包装
  - 测试ApiErrorResponse的错误处理
  - 测试PageVO的分页数据包装
  - _需求: 1.1, 1.2, 8.1_

- [ ] 9.3 编写枚举转换器单元测试
  - 测试所有枚举值的正确转换
  - 测试边界情况和异常处理
  - 验证大小写转换的正确性
  - _需求: 9.1, 9.2_

- [ ] 10. 编写集成测试
  - 编写Controller层集成测试验证完整的请求响应流程
  - 测试分页查询、错误处理等功能
  - _需求: 所有需求的集成测试_

- [ ] 10.1 编写Controller集成测试
  - 为UserController编写集成测试，验证API响应格式
  - 为CourseController编写集成测试，验证课程和选课API
  - 为QuestionController编写集成测试，验证题目查询和搜索
  - 为AssignmentController编写集成测试，验证作业和提交流程
  - _需求: 1.1, 2.1, 3.1, 4.1, 5.1_

- [ ] 10.2 编写分页查询集成测试
  - 测试所有分页API返回正确的PageVO格式
  - 验证分页参数的正确处理
  - 测试排序和过滤功能
  - _需求: 8.1, 8.2, 8.3_

- [ ] 10.3 编写错误处理集成测试
  - 测试各种错误场景返回正确的ApiErrorResponse格式
  - 验证错误代码和错误消息的准确性
  - 测试异常处理的完整性
  - _需求: 1.3_

- [ ] 11. 性能优化和验证
  - 优化VO映射器性能，验证响应时间符合要求
  - 进行负载测试确保新的响应格式不影响性能
  - _需求: 性能要求_

- [ ] 11.1 优化VO映射器性能
  - 使用MapStruct自动生成映射代码替换手动映射
  - 优化数据库查询，避免N+1问题
  - 实现必要的缓存机制
  - _需求: 性能优化_

- [ ] 11.2 进行性能基准测试
  - 测试新旧响应格式的性能差异
  - 进行负载测试验证系统稳定性
  - 监控内存使用和GC性能
  - _需求: 性能验证_

- [ ] 12. 文档更新和部署准备
  - 更新API文档反映新的响应格式
  - 准备部署脚本和回滚方案
  - _需求: 文档和部署_

- [ ] 12.1 更新API文档
  - 更新所有API端点的响应示例
  - 添加新的VO数据结构说明
  - 更新错误响应格式文档
  - _需求: 文档更新_

- [ ] 12.2 准备部署和回滚方案
  - 创建部署检查清单
  - 准备数据库迁移回滚脚本
  - 制定分阶段部署计划
  - _需求: 部署准备_
建议 1: 将“知识点”实体化、结构化
当前问题: 在 janus_questions 表中，knowledge_points 字段被定义为 TEXT[] (文本数组)。虽然简单直观，但存在几个显著缺点：

查询效率低: 无法高效地查询“掌握某个知识点的所有学生”或“包含特定知识点的所有题目”。

数据不一致: 不同的教师可能会输入相似但略有不同的知识点名称（例如“二分查找” vs “二分法”），导致统计困难。

难以扩展: 无法为知识点添加额外属性，如“难度系数”、“前置知识点”等。

优化方案:

创建一个专门的 janus_knowledge_points 表。

创建一个 janus_question_knowledge_points 多对多关联表。

-- 建议新增: 知识点表
CREATE TABLE janus_knowledge_points (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    subject VARCHAR(100), -- 可选，用于学科分类
    parent_id UUID REFERENCES janus_knowledge_points(id), -- 用于构建知识图谱
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 建议新增: 题目与知识点的多对多关联表
CREATE TABLE janus_question_knowledge_points (
    question_id UUID NOT NULL REFERENCES janus_questions(id) ON DELETE CASCADE,
    knowledge_point_id UUID NOT NULL REFERENCES janus_knowledge_points(id) ON DELETE CASCADE,
    PRIMARY KEY (question_id, knowledge_point_id)
);

-- 同时，从 janus_questions 表中移除 knowledge_points TEXT[] 字段。

这样做的好处: 极大地增强了学情分析的能力，为未来的知识图谱构建和个性化推荐奠定了坚实基础。

建议 2: 为 RAG 流程设计配套的追踪表
当前问题: 项目明确使用 RAG，但当前的数据库 Schema 没有体现对 RAG 流程中关键数据的追踪。例如，一个教案（janus_lesson_plans）是基于哪个源文件（source_document_url）的哪些部分生成的？这些文本块（chunks）对应的向量ID是什么？

优化方案: 设计一张表来存储文档被分割后的文本块及其在向量数据库中的元数据。

-- 建议新增: 文档内容块与向量元数据表
CREATE TABLE janus_document_chunks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- 关联到内容源，这里用 lesson_plan_id 举例，也可以设计得更通用
    lesson_plan_id UUID NOT NULL REFERENCES janus_lesson_plans(id) ON DELETE CASCADE,
    content TEXT NOT NULL, -- 存储分割后的文本块
    chunk_order INTEGER NOT NULL, -- 文本块在原文档中的顺序
    vector_id VARCHAR(255) UNIQUE, -- 在 Redis Vector Store 中的唯一标识符
    metadata JSONB, -- 可存储页码、标题等额外信息
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

这样做的好处:

可追溯性: 可以精确地知道 AI 生成的每一部分内容参考了源文档的哪些具体片段。

可维护性: 当源文档更新时，可以方便地找到并更新或删除 Redis 中对应的向量。

调试能力: 当 AI 生成效果不佳时，可以回溯到具体的上下文（Context），分析是检索出了问题还是生成环节出了问题。

建议 3: 增强 AI 生成任务的元数据记录
当前问题: janus_lesson_plans 表记录了生成任务的状态，但信息有限。

优化方案: 在 janus_lesson_plans 表中增加更多与 AI 调用相关的元数据字段。

-- 建议修改 janus_lesson_plans 表
ALTER TABLE janus_lesson_plans
ADD COLUMN ai_model_used VARCHAR(100), -- 例如 'gpt-4-turbo'
ADD COLUMN ai_prompt TEXT, -- 保存用于生成的最终 Prompt
ADD COLUMN generation_duration_ms BIGINT, -- 生成耗时（毫秒）
ADD COLUMN error_message TEXT; -- 如果失败，记录错误信息

这样做的好-处: 便于分析不同模型/Prompt 的效果，优化成本和性能，以及快速定位生成失败的原因。


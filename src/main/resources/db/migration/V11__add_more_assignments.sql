-- V11__add_more_assignments.sql
-- Add more assignment data to the janus_assignments table

-- Use existing course and teacher from V6__insert_test_data.sql
DO $$
DECLARE
    -- ID for course '计算机科学基础'
    v_course_id UUID := 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11';
    -- ID for user 'teacher_zhang'
    v_teacher_id UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11';
BEGIN
    INSERT INTO janus_assignments (id, title, description, course_id, creator_id, due_date) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '计算机组成原理3', '完成阿姆达尔定律相关的练习题', v_course_id, v_teacher_id, '2024-12-20 23:59:59'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', '操作系统概念', '阅读第五章：进程同步', v_course_id, v_teacher_id, '2024-11-30 23:59:59'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', '数据库系统', '设计一个简单的电商数据库模式', v_course_id, v_teacher_id, '2025-01-15 23:59:59'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '计算机网络', '实现一个简单的TCP客户端/服务器', v_course_id, v_teacher_id, '2025-02-10 23:59:59'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '数据结构与算法', '完成二叉搜索树的实现和测试', v_course_id, v_teacher_id, '2024-12-25 23:59:59'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', '软件工程导论', '撰写一份软件需求规格说明书', v_course_id, v_teacher_id, '2025-03-01 23:59:59');
END $$; 
-- V6__insert_test_data.sql
-- This script inserts test data based on the frontend's mock data.
-- Note: UUIDs are hardcoded for consistency across tables.

-- Insert Users (janus_users)
-- Passwords are 'password' bcrypted. Online tool used for generation.
INSERT INTO janus_users (id, username, password_hash, email, display_name, avatar_url, role, created_at, updated_at) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'teacher_zhang', '$2a$10$Y.uV5e.z.h.wz7.d8j8u5.Jd8.I.Y.sS6U.9U/0I.4L6O8G8k3g.6', 'zhang@example.com', '张老师', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop&crop=face', 'teacher', NOW(), NOW()),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'student_li', '$2a$10$Y.uV5e.z.h.wz7.d8j8u5.Jd8.I.Y.sS6U.9U/0I.4L6O8G8k3g.6', 'li@example.com', '李小明', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face', 'student', NOW(), NOW()),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'admin_wang', '$2a$10$Y.uV5e.z.h.wz7.d8j8u5.Jd8.I.Y.sS6U.9U/0I.4L6O8G8k3g.6', 'wang@example.com', '王管理员', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face', 'admin', NOW(), NOW());

-- Insert Courses (janus_courses)
INSERT INTO janus_courses (id, name, description, teacher_id, created_at, updated_at) VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', '计算机科学基础', '学习计算机科学的基本概念和原理', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW(), NOW()),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'Python编程入门', '从零开始学习Python编程语言', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW(), NOW());

-- Insert Lesson Plans (janus_lesson_plans)
-- This will ensure the /api/syllabus endpoint returns data.
INSERT INTO janus_lesson_plans (id, name, course_id, creator_id, status, created_at, updated_at) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 'Python基础课程大纲', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'completed', NOW(), NOW());

-- Insert Lesson Plan Items (janus_lesson_plan_items)
INSERT INTO janus_lesson_plan_items (id, lesson_plan_id, title, content_type, content, "order", created_at, updated_at) VALUES
(gen_random_uuid(), 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 'Python基础', 'lecture', '学习Python的基本语法和概念', 1, NOW(), NOW()),
(gen_random_uuid(), 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', '数据结构', 'lecture', '学习Python中的数据结构，包括列表、字典、元组等', 2, NOW(), NOW()),
(gen_random_uuid(), 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', '控制流', 'lecture', '学习条件语句、循环语句等控制流结构', 3, NOW(), NOW());

-- Insert Storage Objects (janus_storage_objects)
-- This will ensure the /api/resources endpoint has data.
INSERT INTO janus_storage_objects (id, object_key, original_filename, file_size, content_type, storage_provider, bucket_name, embedding_status, uploader_id, created_at, updated_at) VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'documents/cs_intro.ppt', '计算机科学导论.ppt', 1024, 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 'local', 'janus-bucket', 'PENDING', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW(), NOW()),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380d12', 'documents/midterm_exam.pdf', '期中考试试卷.pdf', 512, 'application/pdf', 'local', 'janus-bucket', 'PENDING', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW(), NOW());

-- Note: Other tables like questions, assignments, etc., can be populated similarly if needed. 
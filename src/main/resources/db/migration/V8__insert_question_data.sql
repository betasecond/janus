-- V8__insert_question_data.sql
-- This script inserts test data for knowledge points and questions.
-- UUIDs are hardcoded to ensure stable linking between tables.

-- Step 1: Insert Knowledge Points (janus_knowledge_points)
INSERT INTO janus_knowledge_points (id, name, subject) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '地理', 'General'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '欧洲', 'Geography'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', '自然', 'Geography'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', '艺术', 'History'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', '化学', 'Science');

-- Step 2: Insert Questions (janus_questions)
-- The content and correctAnswer are stored in JSONB format.
INSERT INTO janus_questions (id, type, difficulty, content, correct_answer, explanation, creator_id) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'multiple-choice', 'easy',
 '{"title": "法国的首都是什么？", "options": ["伦敦", "柏林", "巴黎", "马德里"]}',
 '{"answer": 2}',
 '巴黎是法国的首都和最大城市。',
 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'multiple-choice', 'medium',
 '{"title": "世界上最高的山峰是什么？", "options": ["珠穆朗玛峰", "乔戈里峰", "干城章嘉峰", "洛子峰"]}',
 '{"answer": 0}',
 NULL,
 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'multiple-choice', 'medium',
 '{"title": "黄金的化学符号是什么？", "options": ["Go", "Gd", "Au", "Ag"]}',
 '{"answer": 2}',
 NULL,
 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
);

-- Step 3: Link Questions to Knowledge Points (janus_question_knowledge_points)
INSERT INTO janus_question_knowledge_points (question_id, knowledge_point_id) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18'); 
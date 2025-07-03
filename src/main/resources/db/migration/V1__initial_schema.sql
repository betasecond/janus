-- V1__initial_schema.sql
-- Initial schema for the Janus Eye application

-- 1. `janus_users` - User table
-- Stores user information for all roles.
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

-- 2. `janus_courses` - Course table
-- Stores basic course information.
CREATE TABLE janus_courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    teacher_id UUID NOT NULL REFERENCES janus_users(id),
    cover_image_url VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. `janus_course_enrollments` - Course enrollment table
-- Links students to courses.
CREATE TABLE janus_course_enrollments (
    course_id UUID NOT NULL REFERENCES janus_courses(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES janus_users(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (course_id, student_id)
);

-- 4. `janus_questions` - Question bank table
-- Stores all questions as a reusable resource.
CREATE TABLE janus_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL CHECK (type IN ('multiple-choice', 'true-false', 'short-answer', 'essay')),
    difficulty VARCHAR(20) NOT NULL CHECK (difficulty IN ('easy', 'medium', 'hard')),
    knowledge_points TEXT[],
    content JSONB NOT NULL, -- Stores question stem, options, etc.
    correct_answer JSONB,   -- Stores the correct answer.
    explanation TEXT,
    creator_id UUID REFERENCES janus_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. `janus_assignments` - Assignment table
-- Stores assignment information created by teachers.
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

-- 6. `janus_assignment_questions` - Assignment-question link table
-- Links assignments to questions.
CREATE TABLE janus_assignment_questions (
    assignment_id UUID NOT NULL REFERENCES janus_assignments(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES janus_questions(id) ON DELETE CASCADE,
    "order" INTEGER,
    PRIMARY KEY (assignment_id, question_id)
);

-- 7. `janus_assignment_submissions` - Assignment submission table
-- Stores student submission records for assignments.
CREATE TABLE janus_assignment_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID NOT NULL REFERENCES janus_assignments(id),
    student_id UUID NOT NULL REFERENCES janus_users(id),
    submitted_at TIMESTAMPTZ DEFAULT NOW(),
    status VARCHAR(20) DEFAULT 'submitted' CHECK (status IN ('submitted', 'grading', 'graded')),
    score NUMERIC(5, 2),
    UNIQUE(assignment_id, student_id)
);

-- 8. `janus_submission_answers` - Submission answer table
-- Stores the specific answers provided by a student for a submission.
CREATE TABLE janus_submission_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID NOT NULL REFERENCES janus_assignment_submissions(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES janus_questions(id),
    answer JSONB,
    is_correct BOOLEAN
);

-- 9. `janus_lesson_plans` - Lesson plan table
-- Stores lesson plans created by teachers.
CREATE TABLE janus_lesson_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    course_id UUID REFERENCES janus_courses(id),
    creator_id UUID NOT NULL REFERENCES janus_users(id),
    source_document_url VARCHAR(512),
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'generating', 'completed', 'failed')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 10. `janus_lesson_plan_items` - Lesson plan item table
-- Stores the specific chapters or content blocks of a lesson plan.
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

-- Add indexes for foreign keys to improve query performance
CREATE INDEX idx_courses_teacher_id ON janus_courses(teacher_id);
CREATE INDEX idx_enrollments_student_id ON janus_course_enrollments(student_id);
CREATE INDEX idx_questions_creator_id ON janus_questions(creator_id);
CREATE INDEX idx_assignments_course_id ON janus_assignments(course_id);
CREATE INDEX idx_submissions_student_id ON janus_assignment_submissions(student_id);
CREATE INDEX idx_answers_submission_id ON janus_submission_answers(submission_id);
CREATE INDEX idx_lesson_plans_creator_id ON janus_lesson_plans(creator_id);
CREATE INDEX idx_lesson_plan_items_lesson_plan_id ON janus_lesson_plan_items(lesson_plan_id); 
-- V9__fix_question_enum_data.sql
-- This script corrects the casing of enum-like string data in the janus_questions table
-- to align with the uppercase Kotlin Enum definitions.

-- Step 1: Drop the old, lowercase CHECK constraints to allow data modification.
-- The constraint names are inferred from the table and column names as per V1.
ALTER TABLE janus_questions DROP CONSTRAINT janus_questions_difficulty_check;
ALTER TABLE janus_questions DROP CONSTRAINT janus_questions_type_check;

-- Step 2: Update existing data to uppercase.
-- This is now safe because the constraints have been removed.
UPDATE janus_questions SET difficulty = UPPER(difficulty) WHERE difficulty IN ('easy', 'medium', 'hard');
UPDATE janus_questions SET type = UPPER(type) WHERE type IN ('multiple-choice', 'true-false', 'short-answer', 'essay');

-- Note: As per the plan, new constraints are NOT added in this script.
-- This can be done in a subsequent migration (e.g., V10) if desired. 
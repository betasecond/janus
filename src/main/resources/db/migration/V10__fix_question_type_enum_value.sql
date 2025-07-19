-- V10__fix_question_type_enum_value.sql
-- This script corrects the value of the 'type' field in the janus_questions table.
-- The UPPER() function in V9 converted 'multiple-choice' to 'MULTIPLE-CHOICE',
-- but the corresponding Kotlin Enum is 'MULTIPLE_CHOICE' (with an underscore).
-- This script replaces the hyphen with an underscore to match the Enum definition.

UPDATE janus_questions
SET type = 'MULTIPLE_CHOICE'
WHERE type = 'MULTIPLE-CHOICE'; 
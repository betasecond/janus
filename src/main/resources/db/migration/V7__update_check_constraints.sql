-- V7__update_check_constraints.sql
-- This script updates the CHECK constraints for status and content_type fields to use uppercase values,
-- aligning the database with the Kotlin Enum definitions.
-- The correct order is: DROP constraint -> UPDATE data -> ADD new constraint.

-- Step 1: Drop the old, lowercase constraints first to allow data modification.
ALTER TABLE janus_lesson_plans DROP CONSTRAINT janus_lesson_plans_status_check;
ALTER TABLE janus_lesson_plan_items DROP CONSTRAINT janus_lesson_plan_items_content_type_check;

-- Step 2: Update existing data to uppercase. This is safe now because the constraints are gone.
-- This is crucial because V6 inserted lowercase data.
UPDATE janus_lesson_plans SET status = UPPER(status) WHERE status IN ('draft', 'generating', 'completed', 'failed');
UPDATE janus_lesson_plan_items SET content_type = UPPER(content_type) WHERE content_type IN ('lecture', 'exercise', 'note', 'summary');

-- Step 3: Add the new, correct, uppercase constraints.
ALTER TABLE janus_lesson_plans ADD CONSTRAINT janus_lesson_plans_status_check
CHECK (status IN ('DRAFT', 'GENERATING', 'COMPLETED', 'FAILED'));
ALTER TABLE janus_lesson_plan_items ADD CONSTRAINT janus_lesson_plan_items_content_type_check
CHECK (content_type IN ('LECTURE', 'EXERCISE', 'NOTE', 'SUMMARY'));

-- Step 4: Update the default value to uppercase for consistency.
ALTER TABLE janus_lesson_plans ALTER COLUMN status SET DEFAULT 'DRAFT'; 
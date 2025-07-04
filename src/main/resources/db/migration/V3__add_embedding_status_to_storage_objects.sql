-- V3__add_embedding_status_to_storage_objects.sql
-- Adds an embedding status to the storage objects table to track processing state.

ALTER TABLE janus_storage_objects
ADD COLUMN embedding_status VARCHAR(50) NOT NULL DEFAULT 'PENDING';

COMMENT ON COLUMN janus_storage_objects.embedding_status IS 'The embedding status of the file (e.g., PENDING, COMPLETED, FAILED).'; 
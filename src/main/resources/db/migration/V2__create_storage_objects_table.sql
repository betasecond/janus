-- V2__create_storage_objects_table.sql
-- Creates a table to store metadata for objects in OSS

CREATE TABLE janus_storage_objects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_key VARCHAR(1024) NOT NULL UNIQUE,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(128),
    storage_provider VARCHAR(50) NOT NULL DEFAULT 'ALIYUN_OSS',
    bucket_name VARCHAR(255) NOT NULL,
    uploader_id UUID REFERENCES janus_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON COLUMN janus_storage_objects.object_key IS 'The unique key/path of the object in the OSS bucket.';
COMMENT ON COLUMN janus_storage_objects.uploader_id IS 'The user who uploaded the file.';

CREATE INDEX idx_storage_objects_uploader_id ON janus_storage_objects(uploader_id); 
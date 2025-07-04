-- V4__insert_test_user.sql
-- Inserts a test user to ensure that tests requiring a user can run successfully.

INSERT INTO janus_users (id, username, password_hash, email, display_name, role)
VALUES (
    '388e9514-8840-4528-a28a-f5b2b2796d88',
    'testuploader',
    'a-dummy-password-hash',
    'test.uploader@example.com',
    'Test Uploader',
    'teacher'
) ON CONFLICT (id) DO NOTHING; 
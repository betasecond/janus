-- V5__add_notifications_table.sql
-- 添加通知功能表

CREATE TABLE janus_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('assignment', 'grade', 'system', 'course')),
    sender_id UUID REFERENCES janus_users(id),
    recipient_id UUID NOT NULL REFERENCES janus_users(id),
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 创建索引提高查询性能
CREATE INDEX idx_notifications_recipient_id ON janus_notifications(recipient_id);
CREATE INDEX idx_notifications_is_read ON janus_notifications(is_read);
CREATE INDEX idx_notifications_type ON janus_notifications(type);
CREATE INDEX idx_notifications_created_at ON janus_notifications(created_at);

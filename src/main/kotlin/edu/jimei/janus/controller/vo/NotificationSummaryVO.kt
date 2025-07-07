package edu.jimei.janus.controller.vo

import edu.jimei.janus.domain.notification.NotificationType

data class NotificationSummaryVO(
    val totalCount: Long,
    val unreadCount: Long,
    val byType: Map<NotificationType, Long>
) 
package edu.jimei.janus.controller.vo

import edu.jimei.janus.domain.notification.NotificationType

data class NotificationStatsVO(
    val total: Int,
    val byType: Map<NotificationType, Int>,
    val readStatus: Map<String, Int>
) 
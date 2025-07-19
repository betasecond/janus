package edu.jimei.janus.controller.vo

import edu.jimei.janus.domain.user.User
import java.time.LocalDateTime
import java.util.UUID

data class UserVO(
    val id: UUID,
    val username: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val role: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

fun User.toVo(): UserVO = UserVO(
    id = this.id!!,
    username = this.username,
    email = this.email,
    displayName = this.displayName,
    avatarUrl = this.avatarUrl,
    role = this.role,
    createdAt = this.createdAt!!,
    updatedAt = this.updatedAt!!
) 
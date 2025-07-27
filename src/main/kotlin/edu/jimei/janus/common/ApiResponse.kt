package edu.jimei.janus.common

/**
 * 统一API成功响应包装器
 * @param T 响应数据类型
 * @property success 请求是否成功，固定为true
 * @property data 响应数据
 */
data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T
)
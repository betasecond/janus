package edu.jimei.janus.common

/**
 * 统一API错误响应包装器
 * @property success 请求是否成功，固定为false
 * @property error 错误详情
 */
data class ApiErrorResponse(
    val success: Boolean = false,
    val error: ErrorDetail
)

/**
 * 错误详情
 * @property code 错误代码
 * @property message 错误消息
 */
data class ErrorDetail(
    val code: String,
    val message: String
)
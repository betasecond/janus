package edu.jimei.janus.common

/**
 * 标准化错误代码枚举
 * @property code 错误代码
 * @property message 默认错误消息
 */
enum class ErrorCode(val code: String, val message: String) {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "The requested resource was not found"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Input validation failed"),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication required"),
    FORBIDDEN("FORBIDDEN", "Access denied"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred"),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "Resource already exists"),
    INVALID_OPERATION("INVALID_OPERATION", "Operation not allowed in current state"),
    BAD_REQUEST("BAD_REQUEST", "Invalid request parameters"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "HTTP method not allowed"),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "Unsupported media type")
}
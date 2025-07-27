package edu.jimei.janus.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.dao.DataIntegrityViolationException
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory

/**
 * 全局异常处理器
 * 确保所有异常都返回统一的ApiErrorResponse格式
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    /**
     * 处理实体未找到异常
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Entity not found: {}", ex.message)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.RESOURCE_NOT_FOUND.code,
                message = ex.message ?: ErrorCode.RESOURCE_NOT_FOUND.message
            )
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }
    

    
    /**
     * 处理方法参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Method argument validation error: {}", ex.message)
        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.VALIDATION_ERROR.code,
                message = errorMessage.ifEmpty { ErrorCode.VALIDATION_ERROR.message }
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }
    
    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Method argument type mismatch: {}", ex.message)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.BAD_REQUEST.code,
                message = "Invalid parameter type for '${ex.name}'"
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }
    
    /**
     * 处理数据完整性违反异常（如唯一约束违反）
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Data integrity violation: {}", ex.message)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.DUPLICATE_RESOURCE.code,
                message = ErrorCode.DUPLICATE_RESOURCE.message
            )
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }
    

    
    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(ex: NoHandlerFoundException): ResponseEntity<ApiErrorResponse> {
        logger.warn("No handler found: {}", ex.message)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.RESOURCE_NOT_FOUND.code,
                message = "Endpoint not found: ${ex.requestURL}"
            )
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Illegal argument: {}", ex.message)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.BAD_REQUEST.code,
                message = ex.message ?: ErrorCode.BAD_REQUEST.message
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }
    
    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Illegal state: {}", ex.message)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.INVALID_OPERATION.code,
                message = ex.message ?: ErrorCode.INVALID_OPERATION.message
            )
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }
    
    /**
     * 处理所有其他未捕获的异常
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        val error = ApiErrorResponse(
            error = ErrorDetail(
                code = ErrorCode.INTERNAL_SERVER_ERROR.code,
                message = ErrorCode.INTERNAL_SERVER_ERROR.message
            )
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
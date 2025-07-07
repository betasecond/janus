package edu.jimei.janus.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@Schema(description = "分页响应的统一结构")
data class PageDto<T>(
    @Schema(description = "当前页的数据列表")
    val content: List<T>,

    @Schema(description = "总元素数量")
    val totalElements: Long,

    @Schema(description = "总页数")
    val totalPages: Int,

    @Schema(description = "每页大小")
    val size: Int,

    @Schema(description = "当前页码 (从0开始)")
    val number: Int
)

/**
 * 将 Spring Data 的 Page 对象转换为我们自定义的 PageDto。
 * @param T Page 中内容的类型。
 * @param R DTO 中内容的类型。
 * @param contentMapper 将领域模型转换为 DTO 的映射函数。
 * @return 转换后的 PageDto。
 */
fun <T, R> Page<T>.toDto(contentMapper: (T) -> R): PageDto<R> {
    return PageDto(
        content = this.content.map(contentMapper),
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        size = this.size,
        number = this.number
    )
}

data class StatusResponseDto(val status: String, val message: String) 
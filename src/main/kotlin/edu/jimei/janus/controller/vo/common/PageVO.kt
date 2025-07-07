package edu.jimei.janus.controller.vo.common

import org.springframework.data.domain.Page

data class PageVO<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)

fun <T, U> Page<T>.toVo(mapper: (T) -> U): PageVO<U> {
    return PageVO(
        content = this.content.map(mapper),
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        size = this.size,
        number = this.number
    )
} 
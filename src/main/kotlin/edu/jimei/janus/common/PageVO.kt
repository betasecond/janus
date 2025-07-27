package edu.jimei.janus.common

/**
 * 分页数据包装器
 * @param T 分页内容类型
 * @property content 分页内容列表
 * @property totalElements 总元素数
 * @property totalPages 总页数
 * @property size 每页大小
 * @property number 当前页码（从0开始）
 */
data class PageVO<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)
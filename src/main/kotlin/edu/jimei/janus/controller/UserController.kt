package edu.jimei.janus.controller

import edu.jimei.janus.common.ApiResponse
import edu.jimei.janus.common.PageVO
import edu.jimei.janus.controller.mapper.UserVOMapper
import edu.jimei.janus.controller.vo.UserVO
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * 用户控制器
 * 提供用户相关的API端点，使用统一的响应格式
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository,
    private val userVOMapper: UserVOMapper
) {

    /**
     * 获取所有用户列表
     * @return ApiResponse<List<UserVO>> 包装的用户列表
     */
    @GetMapping
    fun getAllUsers(): ApiResponse<List<UserVO>> {
        val users = userRepository.findAll()
        val userVOs = users.map { userVOMapper.toVO(it) }
        return ApiResponse(data = userVOs)
    }

    /**
     * 分页获取用户列表
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sort 排序字段，默认按创建时间降序
     * @return ApiResponse<PageVO<UserVO>> 包装的分页用户数据
     */
    @GetMapping("/page")
    fun getUsersPage(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sort: String,
        @RequestParam(defaultValue = "desc") direction: String
    ): ApiResponse<PageVO<UserVO>> {
        val sortDirection = if (direction.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
        
        val userPage = userRepository.findAll(pageable)
        val userVOs = userPage.content.map { userVOMapper.toVO(it) }
        
        val pageVO = PageVO(
            content = userVOs,
            totalElements = userPage.totalElements,
            totalPages = userPage.totalPages,
            size = userPage.size,
            number = userPage.number
        )
        
        return ApiResponse(data = pageVO)
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return ApiResponse<UserVO> 包装的用户信息
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ApiResponse<UserVO> {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User with id $id not found") }
        
        val userVO = userVOMapper.toVO(user)
        return ApiResponse(data = userVO)
    }
} 
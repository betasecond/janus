package edu.jimei.janus.controller.mapper

import edu.jimei.janus.common.EnumConverter
import edu.jimei.janus.controller.vo.UserVO
import edu.jimei.janus.domain.user.User
import org.springframework.stereotype.Component

/**
 * 用户VO映射器
 * 负责将User实体映射为UserVO，确保字段名称对齐和枚举值转换
 */
@Component
class UserVOMapper(
    private val enumConverter: EnumConverter
) {
    
    /**
     * 将User实体转换为UserVO
     * @param user User实体
     * @return UserVO 前端视图对象
     */
    fun toVO(user: User): UserVO {
        return UserVO(
            id = user.id.toString(),
            displayName = user.displayName ?: user.username, // 优先使用displayName，否则使用username
            email = user.email,
            avatarUrl = user.avatarUrl ?: "", // 如果avatarUrl为null，返回空字符串
            role = enumConverter.convertUserRole(user.role) // 转换为大写格式
        )
    }
}
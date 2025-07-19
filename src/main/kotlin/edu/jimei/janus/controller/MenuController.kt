package edu.jimei.janus.controller

import edu.jimei.janus.controller.vo.MenuItemVO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/menu")
class MenuController {

    @GetMapping("/teacher")
    fun getTeacherMenu(): ResponseEntity<List<MenuItemVO>> {
        val menuItems = listOf(
            MenuItemVO(id = "1", label = "主页", icon = "home", path = "/home", isActive = true),
            MenuItemVO(id = "2", label = "课程准备", icon = "book", path = "/syllabus"),
            MenuItemVO(id = "3", label = "作业生成", icon = "file", path = "/question"),
            MenuItemVO(id = "4", label = "学习分析", icon = "chart", path = "/overview"),
            MenuItemVO(id = "5", label = "资源管理", icon = "folder", path = "/home"),
            MenuItemVO(id = "6", label = "个人中心", icon = "user", path = "/home")
        )
        return ResponseEntity.ok(menuItems)
    }

    @GetMapping("/student")
    fun getStudentMenu(): ResponseEntity<List<MenuItemVO>> {
        val menuItems = listOf(
            MenuItemVO(id = "1", label = "主页", icon = "home", path = "/home", isActive = true),
            MenuItemVO(id = "2", label = "我的作业", icon = "video", path = "/assignments"),
            MenuItemVO(id = "3", label = "练习评估", icon = "file", path = "/practice"),
            MenuItemVO(id = "4", label = "资源库", icon = "bookmark", path = "/library"),
            MenuItemVO(id = "5", label = "个人中心", icon = "user", path = "/profile")
        )
        return ResponseEntity.ok(menuItems)
    }

    @GetMapping("/admin")
    fun getAdminMenu(): ResponseEntity<List<MenuItemVO>> {
        val menuItems = listOf(
            MenuItemVO(id = "1", label = "主页", icon = "home", path = "/admin/dashboard"),
            MenuItemVO(id = "2", label = "用户管理", icon = "users", path = "/admin/users"),
            MenuItemVO(id = "3", label = "资源管理", icon = "folder", path = "/admin/resources"),
            MenuItemVO(id = "4.ts", label = "系统设置", icon = "gear", path = "/admin/settings")
        )
        return ResponseEntity.ok(menuItems)
    }
} 
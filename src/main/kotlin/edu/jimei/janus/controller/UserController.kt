package edu.jimei.janus.controller

import edu.jimei.janus.controller.vo.UserVO
import edu.jimei.janus.controller.vo.toVo
import edu.jimei.janus.domain.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userRepository: UserRepository) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserVO>> {
        val users = userRepository.findAll()
        val userVos = users.map { it.toVo() }
        return ResponseEntity.ok(userVos)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserVO> {
        return userRepository.findById(id)
            .map { user -> ResponseEntity.ok(user.toVo()) }
            .orElse(ResponseEntity.notFound().build())
    }
} 
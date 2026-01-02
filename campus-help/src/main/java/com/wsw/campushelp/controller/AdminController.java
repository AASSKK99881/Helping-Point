package com.wsw.campushelp.controller;

import com.wsw.campushelp.entity.User;
import com.wsw.campushelp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有用户列表
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        List<User> users = userRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", users);
        return result;
    }

    /**
     * 重置密码为 123456
     * POST /api/admin/users/{id}/reset-password
     */
    @PostMapping("/users/{id}/reset-password")
    public Map<String, Object> resetPassword(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            user.setPassword("123456"); // 简单重置，实际项目需加密
            userRepository.save(user);
            result.put("code", 200);
            result.put("msg", "密码已重置为 123456");
        } else {
            result.put("code", 400);
            result.put("msg", "用户不存在");
        }
        return result;
    }

    /**
     * 删除用户
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "用户已删除");
        return result;
    }

    /**
     * 搜索用户 (按关键字)
     * GET /api/admin/users/search?keyword=xxx
     */
    @GetMapping("/users/search")
    public Map<String, Object> searchUsers(@RequestParam String keyword) {
        List<User> list = userRepository.findByUsernameContaining(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }
}
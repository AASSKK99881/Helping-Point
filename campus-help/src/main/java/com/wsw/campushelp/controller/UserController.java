package com.wsw.campushelp.controller;

import com.wsw.campushelp.common.Result;
import com.wsw.campushelp.entity.User;
import com.wsw.campushelp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户信息 (用于前端显示积分等)
     */
    @GetMapping("/{id}")
    public Result getUserInfo(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 为了安全，把密码设为空再返回（防止密码泄露）
        user.setPassword(null);
        return Result.success(user);
    }
}
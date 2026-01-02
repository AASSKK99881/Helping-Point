package com.wsw.campushelp.controller;

import com.wsw.campushelp.entity.User;
import com.wsw.campushelp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // 接口前缀，访问地址变成 /api/auth/xxx
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 注册接口
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User newUser = userService.register(user);
            result.put("code", 200);
            result.put("msg", "注册成功");
            result.put("data", newUser);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("msg", e.getMessage()); // 比如"用户名已存在"
        }
        return result;
    }

    /**
     * 登录接口
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> result = new HashMap<>();
        // 调用 Service 进行登录
        String token = userService.login(loginUser.getUsername(), loginUser.getPassword());

        if (token != null) {
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token); // 返回最重要的 Token
        } else {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }
}
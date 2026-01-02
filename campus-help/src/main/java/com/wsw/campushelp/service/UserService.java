package com.wsw.campushelp.service;

import com.wsw.campushelp.entity.User;
import com.wsw.campushelp.repository.UserRepository;
import com.wsw.campushelp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录功能
     * @return 如果成功返回 Token，失败返回 null
     */
    public String login(String username, String password) {
        // 1. 去数据库查这个用户
        User user = userRepository.findByUsername(username);

        // 2. 检查用户是否存在，以及密码是否对 (这里演示用明文对比，实际项目应加密)
        if (user != null && user.getPassword().equals(password)) {
            // 3. 登录成功，生成并返回 Token
            return jwtUtils.generateToken(user.getUsername(), user.getRole());
        }

        // 登录失败
        return null;
    }

    /**
     * 注册功能
     */
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置默认值
        if (user.getRole() == null) user.setRole("USER");
        if (user.getPoints() == null) user.setPoints(100);

        return userRepository.save(user);
    }
}
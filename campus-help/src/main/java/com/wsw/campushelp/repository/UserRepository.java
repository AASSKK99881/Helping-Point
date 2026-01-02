package com.wsw.campushelp.repository;

import com.wsw.campushelp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 继承 JpaRepository，自动获得增删改查能力
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 根据用户名查找用户 (用于登录验证)
    // SQL 等价于: select * from user where username = ?
    User findByUsername(String username);

    // 2. 根据关键字模糊查询用户 (用于管理员搜索)
    // SQL 等价于: select * from user where username like %keyword%
    List<User> findByUsernameContaining(String keyword);
}
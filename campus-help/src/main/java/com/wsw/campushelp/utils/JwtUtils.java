package com.wsw.campushelp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // 1. 定义秘钥 (这相当于你的"私章"，不能告诉别人)
    // 必须足够长，否则报错。这里用了一个演示用的强秘钥
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 2. 令牌有效期 (这里设为 7 天: 1000毫秒 * 60秒 * 60分 * 24小时 * 7天)
    private static final long EXPIRE_TIME = 604800000;

    /**
     * 生成 Token
     * @param username 用户名
     * @param role 角色 (ADMIN 或 USER)
     * @return 加密后的字符串
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username) // 存用户名
                .claim("role", role)  // 存角色信息
                .setIssuedAt(new Date()) // 发行时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 过期时间
                .signWith(KEY) // 盖章签名
                .compact();
    }

    /**
     * 解析 Token (验证令牌是不是伪造的)
     * @param token 令牌字符串
     * @return 如果成功返回令牌里的信息，失败抛出异常
     */
    public Claims getClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
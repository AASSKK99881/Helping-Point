package com.wsw.campushelp.config;

import com.wsw.campushelp.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 请求 (浏览器预检请求，必须放行，否则跨域会失败)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 获取请求头中的 Token
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasLength(token)) {
            response.setStatus(401); // 401 代表未授权
            response.getWriter().write("No Token");
            return false; // 拦截
        }

        // 3. 验证 Token 是否合法
        try {
            Claims claims = jwtUtils.getClaimsByToken(token);
            // 可以把用户信息存入 request，方便后续 Controller 使用
            request.setAttribute("currentUserRole", claims.get("role"));
            request.setAttribute("currentUserName", claims.getSubject());
            return true; // 放行
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("Invalid Token");
            return false; // 拦截
        }
    }
}
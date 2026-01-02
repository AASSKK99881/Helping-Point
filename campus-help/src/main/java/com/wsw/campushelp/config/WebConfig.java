package com.wsw.campushelp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")  // 拦截 /api 下的所有请求
                .excludePathPatterns(        // 排除掉不需要登录的接口
                        "/api/auth/login",   // 登录接口
                        "/api/auth/register" // 注册接口
                );
    }
}
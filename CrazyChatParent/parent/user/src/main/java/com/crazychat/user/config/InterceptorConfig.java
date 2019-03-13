package com.crazychat.user.config;

import com.crazychat.user.intercepter.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {
    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 声明拦截的对象和拦截得请求
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")    // 需要拦截的路径
                .excludePathPatterns("/**/login/**", "/**/register/**", "/**/send_email/**");    // 不拦截得到路径
    }
}

package com.crazychat.user.intercepter;

import com.crazychat.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 无论如何都放行，具体能不能操作还是在具体操作中去判断
        // 拦截器只负责把请求头中包含token的令牌进行解析
        String headers = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(headers)) {
            // 如果有包含ZW 的头信息，就对其进行解析
            if(headers.startsWith("ZW ")) {
                // 获取token
                final String token = headers.substring(3);
                // 对令牌进行验证
                try {
                    Claims claims = jwtUtils.parseJWT(token);
                    String roles = (String) claims.get("roles");
                    if ("admin".equals(roles)) {
                        request.setAttribute("claims_admin", token);
                    }
                    if ("user".equals(roles)) {
                        request.setAttribute("claims_user", token);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("令牌有误");
                }
            }
        }
        return true;
    }
}

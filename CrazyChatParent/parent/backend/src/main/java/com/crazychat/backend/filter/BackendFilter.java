package com.crazychat.backend.filter;

import com.crazychat.common.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class BackendFilter extends ZuulFilter {
    @Resource
    private JwtUtils jwtUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器内执行的内容
     * return 任何值都表示继续执行
     * setsendzuulResponse(false)表示不再执行
     * @return
     */
    @Override
    public Object run() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 获取request域
        HttpServletRequest request = currentContext.getRequest();
        if (request.getMethod().equals("OPTIONS")) {
            return null;
        }
        // 登录不需要判断
        if (request.getRequestURL().toString().indexOf("login") > 0) {
            return null;
        }

        // 得到头信息
        String header = request.getHeader("Authorization-token");
        if (!StringUtils.isEmpty(header)) {
            if (header.startsWith("ZW ")) {
                String token = header.substring(3);
                try {
                    Claims claims = jwtUtil.parseJWT(token);
                    String role = (String) claims.get("roles");
                    if (role.trim().equals("admin")) {
                        // 把头信息转发下去， 并且放行
                        currentContext.addZuulRequestHeader("Authorization", header);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    currentContext.setSendZuulResponse(false);  // 终止运行
                }
            }
        }
        currentContext.setSendZuulResponse(false);      // 终止运行
        currentContext.setResponseStatusCode(403);      // 权限不足
        currentContext.setResponseBody("权限不足");
        currentContext.getResponse().setContentType("text/html;charset=utf-8");
        return null;
    }
}

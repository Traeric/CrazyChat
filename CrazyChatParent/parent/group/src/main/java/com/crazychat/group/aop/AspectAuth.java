package com.crazychat.group.aop;

import com.crazychat.common.entity.StatusCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AspectAuth {
    /**
     * 在执行之前检查权限
     */
    @Before("execution(public * com.crazychat.group.controller.GroupController.auth*(..))")
    public void before(JoinPoint point) {
        // 获取request
        HttpServletRequest request = (HttpServletRequest) point.getArgs()[0];
        // 检查角色
        if (null == request.getAttribute("claims_admin")) {
            throw new RuntimeException(StatusCode.ACCESSERROR.getMsg());
        }
    }
}

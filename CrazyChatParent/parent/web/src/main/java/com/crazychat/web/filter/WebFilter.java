package com.crazychat.web.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class WebFilter extends ZuulFilter {
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

    @Override
    public Object run() {
        // 得到request上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 得到request域
        HttpServletRequest request = currentContext.getRequest();
        // 得到头信息
        String header = request.getHeader("Authorization");
        // 判断是否有头信息
        if (!StringUtils.isEmpty(header)) {
            // 把头信息继续往下传
            currentContext.addZuulRequestHeader("Authorization", header);
        }
        return null;
    }
}


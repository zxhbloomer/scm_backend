package com.xinyirun.scm.security.requestid;

import com.xinyirun.scm.common.constant.SystemConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class SystemRequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 生成唯一的请求ID
        String requestId = "pc_" + UUID.randomUUID().toString();

        // 将请求ID添加到请求的属性中
        request.setAttribute(SystemConstants.REQUEST_ID, requestId);

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
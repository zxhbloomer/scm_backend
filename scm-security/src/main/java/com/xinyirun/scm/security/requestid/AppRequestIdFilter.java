package com.xinyirun.scm.security.requestid;

import com.xinyirun.scm.common.constant.SystemConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 这个过滤器用于为每个进入系统的HTTP请求生成一个唯一的请求ID。
 * 请求ID在处理整个请求期间是可用的，并且可以用于日志记录和跟踪请求。
 */
public class AppRequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    jakarta.servlet.FilterChain filterChain) throws ServletException, IOException {
        // 生成唯一的请求ID
        String requestId = "app_" + UUID.randomUUID().toString();

        // 将请求ID添加到请求的属性中
        request.setAttribute(SystemConstants.REQUEST_ID, requestId);

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}

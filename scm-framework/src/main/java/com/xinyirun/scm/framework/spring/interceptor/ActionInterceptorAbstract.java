package com.xinyirun.scm.framework.spring.interceptor;

import com.xinyirun.scm.bean.system.result.utils.v1.ResponseResultUtil;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * preHandle
 *
 * 调用时间：Controller方法处理之前
 *
 * 执行顺序：链式Intercepter情况下，Intercepter按照声明的顺序一个接一个执行
 *
 * 若返回false，则中断执行，注意：不会进入afterCompletion
 *
 *
 *
 * postHandle
 *
 * 调用前提：preHandle返回true
 *
 * 调用时间：Controller方法处理完之后，DispatcherServlet进行视图的渲染之前，也就是说在这个方法中你可以对ModelAndView进行操作
 *
 * 执行顺序：链式Intercepter情况下，Intercepter按照声明的顺序倒着执行。
 *
 * 备注：postHandle虽然post打头，但post、get方法都能处理
 *
 *
 *
 * afterCompletion
 *
 * 调用前提：preHandle返回true
 *
 * 调用时间：DispatcherServlet进行视图的渲染之后
 *
 * 多用于清理资源
 *
 * @author zhangxh
 */
@Slf4j
public abstract class ActionInterceptorAbstract implements HandlerInterceptor {

    /**
     * 该方法将在Controller处理之前进行调用
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 处理BeforeAction：比如：记录日志、参数验证、权限验证
        log.debug("===========Controller前进行调用preHandle操作 开始===========");

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmitAnnotion annotation = method.getAnnotation(RepeatSubmitAnnotion.class);
            if (annotation != null ) {
                if (this.isRepeatSubmit(request)) {
                    ResponseResultUtil.responseWriteError(
                            request,
                            response,
                            new BusinessException("不允许重复提交，请稍后再试！"),
                            HttpStatus.SERVICE_UNAVAILABLE.value(),
                            ResultEnum.SYSTEM_REPEAT_SUBMIT,
                            "不允许重复提交，请稍后再试!");
                    log.debug("===========Controller前进行调用preHandle操作 结束===========");
                    // 只有返回true才会继续向下执行，返回false取消当前请求
                    return false;
                }
            }
            return true;
        } else {
            log.debug("===========Controller前进行调用preHandle操作 结束===========");
            // 只有返回true才会继续向下执行，返回false取消当前请求
//            return super.preHandle(request, response, handler);
            return true;
        }
    }

    /**
     * 在Controller的方法调用之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.debug("===========Controller的方法调用之后执行postHandle操作 开始===========");
        log.debug("===========Controller的方法调用之后执行postHandle操作 结束===========");
    }

    /**
     * 整个请求处理完毕回调方法
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        log.debug("===========Controller整个请求处理完毕回调方法afterCompletion操作 开始===========");
        log.debug("===========Controller整个请求处理完毕回调方法afterCompletion操作 结束===========");
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request
     * @return
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request) throws Exception;
}
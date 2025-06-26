package com.xinyirun.scm.common.utils.annotation;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 *  该类的作用可以把方法上的参数绑定到注解的变量中,注解的语法#{变量名}
 *  能解析类似#{task}或者#{task.taskName}或者{task.project.projectName}
 */
@Slf4j
public class AnnotationResolverUtil {

    private static AnnotationResolverUtil resolver;

    public static AnnotationResolverUtil newInstance() {

        if (resolver == null) {
            return resolver = new AnnotationResolverUtil();
        } else {
            return resolver;
        }

    }

    /**
     * 解析注解上的值
     *
     * @param joinPoint
     * @param ids 需要解析的字符串
     * @return
     */
    public Object resolverIds(JoinPoint joinPoint, String ids) {
        if (ids == null) {
            return null;
        }
        Object value = null;
        /**
         *  如果name匹配上了#{},则把内容当作变量
         */
        if (ids.matches("#\\{\\D*\\}")) {
            String newStr = ids.replaceAll("#\\{", "").replaceAll("\\}", "");
            /**
             * 复杂类型
             */
            if (newStr.contains(".")) {
                try {
                    value = complexResolverIds(joinPoint, newStr);
                } catch (Exception e) {
                    log.error("resolverIds error:", e);
                }
            } else {
                value = simpleResolver(joinPoint, newStr);
            }
        } else { // 非变量
            value = ids;
        }
        return value;
    }

    /**
     * 解析注解上的值
     * 
     * @param joinPoint
     * @param str 需要解析的字符串
     * @return
     */
    public Object resolver(JoinPoint joinPoint, String str) {
        if (str == null) {
            return null;
        }
        Object value = null;
        /**
         *  如果name匹配上了#{},则把内容当作变量
         */
        if (str.matches("#\\{\\D*\\}")) {
            String newStr = str.replaceAll("#\\{", "").replaceAll("\\}", "");
            /**
             * 复杂类型
             */
            if (newStr.contains(".")) {
                try {
                    value = complexResolver(joinPoint, newStr);
                } catch (Exception e) {
                    log.error("resolver error:", e);
                }
            } else {
                value = simpleResolver(joinPoint, newStr);
            }
        } else { // 非变量
            value = str;
        }
        return value;
    }

    /**
     * 复杂值解析
     * @param joinPoint
     * @param str
     * @return
     * @throws Exception
     */
    private Object complexResolverIds(JoinPoint joinPoint, String str) throws Exception {

        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

        String[] names = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String[] strs = str.split("\\.");

        for (int i = 0; i < names.length; i++) {
            if (strs[0].equals(names[i])) {
                Object obj = args[i];
                Method dmethod = obj.getClass().getDeclaredMethod(getMethodName(strs[1]), new Class[0]);
                Object value = dmethod.invoke(args[i]);
                return getValue(value, 1, strs);
            }
        }
        return null;
    }


    /**
     * 复杂值解析
     * @param joinPoint
     * @param str
     * @return
     * @throws Exception
     */
    private Object complexResolver(JoinPoint joinPoint, String str) throws Exception {

        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

        String[] names = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String[] strs = str.split("\\.");

        for (int i = 0; i < names.length; i++) {
            if (strs[0].equals(names[i])) {
                Object obj = args[i];
                Method dmethod = obj.getClass().getDeclaredMethod(getMethodName(strs[1]), new Class[0]);
                Object value = dmethod.invoke(args[i]);
                return getValue(value, 1, strs);
            }
        }
        return null;
    }

    /**
     * 获取值
     * @param obj
     * @param index
     * @param strs
     * @return
     */
    private Object getValue(Object obj, int index, String[] strs) {

        try {
            if (obj != null && index < strs.length - 1) {
                Method method = obj.getClass().getDeclaredMethod(getMethodName(strs[index + 1]), new Class[0]);
                obj = method.invoke(obj);
                getValue(obj, index + 1, strs);
            }

            return obj;

        } catch (Exception e) {
            log.error("getValue error:", e);
            return null;
        }
    }

    /**
     * 方法名
     * @param name
     * @return
     */
    private String getMethodName(String name) {
        return "get" + name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
    }

    /**
     * 简易解析
     * @param joinPoint
     * @param str
     * @return
     */
    private Object simpleResolver(JoinPoint joinPoint, String str) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < names.length; i++) {
            if (str.equals(names[i])) {
                return args[i];
            }
        }
        return null;
    }
}

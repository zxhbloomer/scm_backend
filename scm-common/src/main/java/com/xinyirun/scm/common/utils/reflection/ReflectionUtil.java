package com.xinyirun.scm.common.utils.reflection;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.joor.Reflect;
import org.springframework.util.ReflectionUtils;

import static org.joor.Reflect.on;
import static org.joor.Reflect.onClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReflectionUtil
 * @Description: 反射工具类，提供了反射相关的实用方法。
 * @Author: zxh
 * @date: 2019/10/15
 * @Version: 1.0
 */
@Slf4j
public class ReflectionUtil {


    /**
     * 获取对象的字段的值
     * @param target
     * @param fieldName
     * @return
     */
    public static Object getFieldObject(Object target, String fieldName) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, target);
    }

    /**
     * 获取对象的字段的值
     * @param target
     * @param fieldName
     * @return
     */
    public static <T> T getFieldValue(Object target, String fieldName) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        return (T) ReflectionUtils.getField(field, target);
    }

    /**
     * 设置对象的字段的值
     * @param target
     * @param fieldName
     * @return
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, value);
    }

    /**
     *
     * @param className
     * @param functionName
     * @param data
     * @param <T>
     * @return
     */
//    public static <T> boolean invoke (String className, String functionName,T data)  {
//        if(className == null || "".equals(className.trim())){
//            throw new RefelctionException("未指定class名称");
//        }
//        if(functionName == null || "".equals(functionName.trim())){
//            throw new RefelctionException("未指定方法名称");
//        }
//
//        // 执行方法
//        return onClass(className).create().call(functionName, data).get();
//    }

    public static <T> boolean invoke(String className, String functionName, T data, ArrayList... lists) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object service =  SpringContextUtil.getBean(Class.forName(className));
        Method objMethod = service.getClass().getDeclaredMethod(functionName, getMethodParamsType(data, lists));//获取方法
        //执行方法
        Object rtn = objMethod.invoke(service, data, lists[0]);
        // 执行方法
        return (boolean) rtn;
    }

    /**
     *  获取参数类型
     * @param methodParam
     * @param methodParms
     * @return
     */
    public static Class<?>[] getMethodParamsType(Object methodParam , Object... methodParms) {
        Class<?>[] classs = new Class<?>[methodParms.length + 1];
        classs[0] = methodParam.getClass();
        for(int i = 0; i < methodParms.length; i++) {
            classs[i+1] = methodParms[i].getClass();
        }

        return classs;
    }

    /**
     * 获取参数类型，包含数据，反射
     * @param type
     * @param jsonData
     * @return
     */
    public static Object getClassBean(String type, String jsonData) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class _class = Class.forName(type);
        Object o = _class.newInstance();
        Object obObject = JSONObject.parseObject(jsonData, (Type) o);
        return obObject;
    }


    /**
     * 通过反射调用指定类的方法。
     *
     * @param className     类名
     * @param functionName  方法名
     * @param args          方法参数
     * @return 方法返回结果
     * @throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
     */
    public static Object invokex(String className, String functionName, Object... args)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        // 加载并获取类的实例
        Object service = SpringContextUtil.getBean(Class.forName(className));

        // 获取方法参数的类型
        Class<?>[] paramTypes = null;
        if (args != null) {
            paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                // 这里还需要考虑args[i]为null的情况，因为null没有getClass()方法
                paramTypes[i] = args[i] != null ? args[i].getClass() : String.class;
            }
        } else {
            // 如果args为null，则参数类型数组应该是空的，表示无参数
            paramTypes = new Class<?>[0];
        }

        // 获取方法对象，这里需要处理方法重载的情况
        // 如果有多个重载方法，可能需要更复杂的逻辑来确保获取正确的方法
        Object rtn = null;
        try {
            log.debug("反射方法-类名称：{}", className);
            log.debug("反射方法-方法名称：{}", functionName);
            log.debug("反射方法-参数：{}", JSONObject.toJSONString(args));

            Method objMethod = null;
            Class<?> currentClass = service.getClass();

            while (currentClass != null) {
                try {
                    objMethod = currentClass.getDeclaredMethod(functionName, paramTypes);
                    break;
                } catch (NoSuchMethodException e) {
                    // 在当前类中找不到方法，尝试在父类中查找
                    currentClass = currentClass.getSuperclass();
                }
            }

            if (objMethod == null) {
                throw new NoSuchMethodException("无法找到方法: " + functionName);
            }

            // 设置方法可访问
            objMethod.setAccessible(true);

            // 执行方法
            rtn = objMethod.invoke(service, args);
        } catch (Exception e) {
            log.error("反射方法出错：",e);
            throw new ClassNotFoundException();
        }

        return rtn;
    }


}

//package com.xinyirun.scm.framework.utils.reflection;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.xinyirun.scm.bean.system.v1.ao.reflection.CallInfoReflectionAo;
//import com.xinyirun.scm.framework.utils.spring.SpringContextsUtil;
//import org.joor.Reflect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.ReflectionUtils;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//import static org.joor.Reflect.on;
//
///**
// * @ClassName: ReflectionUtil
// * @Description: 反射类
// * @Author: zxh
// * @date: 2019/10/15
// * @Version: 1.0
// */
//public class ReflectionUtil {
//
//    /**
//     * 考虑数据放到redis中，然后需要回调则考虑回调，没有就没有
//     */
//
//    /**
//     * 这个为spring版本
//     */
//    @Autowired
//    @Deprecated
//    private SpringContextsUtil springContextsUtil;
//
//    public void reInvoke(String beanName, String methodName, String[] params) {
//        Method method = ReflectionUtils
//            .findMethod(springContextsUtil.getBean(beanName).getClass(), methodName, String.class, String.class,
//                Boolean.class, String.class);
//        Object[] param1 = new Object[3];
//        param1[0] = params[0];
//        param1[1] = params[1];
//        param1[2] = true;
//        ReflectionUtils.invokeMethod(method, springContextsUtil.getBean(beanName), param1);
//    }
//
//
//    /**
//     * 字符串反射方式,进行调用
//     *
//     * @param className              类名                   "audit.service.DegreeFunctionTest"
//     * @param functionName           函数名                 "saveDo"
//     * @param parameterClass         参数类型名              "audit.entity.entityTest.TestListBean"
//     * @param parameter              参数数据                '[{"x1":"setData","x2":88888888,"x3":true,"x4":[{"x1":"setSubList1","x2":55555555,"x3":true,"x4":[{"x1":"TestSubSubListBean1","x2":111111,"x3":true},{"x1":"TestSubSubListBean2","x2":222222,"x3":true}]},{"x1":"setSubList2","x2":333333,"x3":true,"x4":[{"x1":"TestSubSubListBean1","x2":111111,"x3":true},{"x1":"TestSubSubListBean2","x2":222222,"x3":true}]}]},{"x1":"setData","x2":7777777,"x3":true,"x4":[{"x1":"setSubList1","x2":55555555,"x3":true,"x4":[{"x1":"TestSubSubListBean1","x2":111111,"x3":true},{"x1":"TestSubSubListBean2","x2":222222,"x3":true}]},{"x1":"setSubList2","x2":333333,"x3":true,"x4":[{"x1":"TestSubSubListBean1","x2":111111,"x3":true},{"x1":"TestSubSubListBean2","x2":222222,"x3":true}]}]}]'
//     * @return
//     */
//    public static boolean invoke(String className, String functionName, String parameterClass, String parameter) {
//        if (className == null || "".equals(className.trim())) {
//            return false;
//        }
//        if (functionName == null || "".equals(functionName.trim())) {
//            return false;
//        }
//
//        // 返回值
//        String rtnString = "";
//
//        // 执行方法
//        rtnString = String.valueOf(on(className).create().call(functionName, parameterClass, parameter));
//        return true;
//    }
//
//    /**
//     * 调用class下的方法
//     *
//     * @param className
//     * @param functionName
//     * @param arg
//     * @return
//     */
//    public static boolean invoke(String className, String functionName, Object arg) {
//        if (className == null || "".equals(className.trim())) {
//            return false;
//        }
//        if (functionName == null || "".equals(functionName.trim())) {
//            return false;
//        }
//
//        // 返回值
//        // 执行方法
//        Object rtn = on(className).create().call(functionName, arg);
//        return true;
//    }
//
//    /**
//     * 通过调用参数bean进行调用，按字符串反射方式
//     *
//     * @param reflectionAo
//     * @return
//     */
//    public static boolean invokeByString(CallInfoReflectionAo reflectionAo) {
//        if(reflectionAo == null){
//            return false;
//        }
//
//        return ReflectionUtil.invoke(reflectionAo.getClassName(),
//                                reflectionAo.getFunctionName(),
//                                reflectionAo.getParameterBeanClass(),
//                                reflectionAo.getParameterJson());
//    }
//
//    /**
//     * 通过调用参数bean进行调用，按字符串反射参数对象后调用
//     *
//     * @param reflectionAo
//     * @return
//     */
//    public static boolean invokeByObject(CallInfoReflectionAo reflectionAo) {
//        if(reflectionAo == null){
//            return false;
//        }
//
//        Object arg = ReflectionUtil.getClassBean(reflectionAo.getParameterBeanClass(), reflectionAo.getParameterJson());
//
//        return ReflectionUtil.invoke(reflectionAo.getClassName(),
//            reflectionAo.getFunctionName(),
//            arg);
//    }
//
//    /**
//     * 获取参数类型，包含数据，反射
//     * @param type
//     * @param jsonData
//     * @return
//     */
//    public static Object getClassBean(String type, String jsonData){
//        Object obClass = on(type).create();
//        Object obObject = JSONObject.parseObject(jsonData, ((Reflect)obClass).type());
//        return obObject;
//    }
//
//    /**
//     * 获取对象的字段的值
//     * @param target
//     * @param fieldName
//     * @return
//     */
//    public static Object getFieldObject(Object target, String fieldName) {
//        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
//        ReflectionUtils.makeAccessible(field);
//        return ReflectionUtils.getField(field, target);
//    }
//
//    /**
//     * 获取对象的字段的值
//     * @param target
//     * @param fieldName
//     * @return
//     */
//    public static <T> T getFieldValue(Object target, String fieldName) {
//        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
//        ReflectionUtils.makeAccessible(field);
//        return (T) ReflectionUtils.getField(field, target);
//    }
//
//    /**
//     * 设置对象的字段的值
//     * @param target
//     * @param fieldName
//     * @return
//     */
//    public static void setFieldValue(Object target, String fieldName, Object value) {
//        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
//        ReflectionUtils.makeAccessible(field);
//        ReflectionUtils.setField(field, target, value);
//    }
//}

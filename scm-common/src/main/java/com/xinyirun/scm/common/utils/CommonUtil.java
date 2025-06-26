package com.xinyirun.scm.common.utils;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.wrapper.MyServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author zxh
 * @create
 *
 **/
@Component
@Slf4j
public final class CommonUtil {

    private static String LOGIN_PROCESSING_URL;

    @Value("${wms.security.code.image.login-processing-url}")
    public void setLOGIN_PROCESSING_URL(String LOGIN_PROCESSING_URL) {
        CommonUtil.LOGIN_PROCESSING_URL = LOGIN_PROCESSING_URL;
    }

    /**
     * 下载文件
     *
     * @param serverFile 服务器端文件路径
     * @param downLoadFile 下载文件的文件名
     * @throws IOException
     */
    public static void download(String serverFile, String downLoadFile, HttpServletResponse response)
            throws IOException {
        /** 获取下载文件的路径 */
        File file = new File(serverFile);
        InputStream fis = new FileInputStream(serverFile);
        /** 获取输出流 */
        OutputStream out = response.getOutputStream();

        /** 设置导出文件名称 */
        downLoadFile = downLoadFile + "_" + DateTimeUtil.dateTimeNow() + SystemConstants.XLSX_SUFFIX;
        downLoadFile = URLEncoder.encode(downLoadFile, "utf-8");
        /** 弹出下载的框filename:提示用户下载的文件名 */
        response.addHeader("content-disposition", "attachment;filename=" + downLoadFile);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("wms-filename", downLoadFile);
        log.debug("获取responseContentType：" + response.getContentType());
        /** 循环读取 */
        byte[] buff = new byte[1024*10];
        int len = 0;
        while((len=fis.read(buff))!=-1){
            out.write(buff,0,len);
        }
        out.flush();

        // 关闭流资源
        fis.close();
        out.close();
        // 删除服务器端生成的下载文件,减轻服务器的压力
        if (file.exists()) {
            if(!file.delete()) {
                throw new BusinessException("文件删除失败");
            }
        }
    }

    /**
     * 下载文件
     *
     * @param serverFile 服务器端文件路径
     * @param downLoadFile 下载文件的文件名
     * @throws IOException
     */
    public static void downloadNormal(String serverFile, String downLoadFile, HttpServletResponse response)
            throws IOException {
        /** 获取下载文件的路径 */
        File file = new File(serverFile);
        InputStream fis = new FileInputStream(serverFile);
        /** 获取输出流 */
        OutputStream out = response.getOutputStream();

        /** 设置导出文件名称 */
        downLoadFile = URLEncoder.encode(downLoadFile, "utf-8");
        /** 弹出下载的框filename:提示用户下载的文件名 */
        response.addHeader("content-disposition", "attachment;filename=" + downLoadFile);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("wms-filename", downLoadFile);
        log.debug("获取responseContentType：" + response.getContentType());
        /** 循环读取 */
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=fis.read(buff))!=-1){
            out.write(buff,0,len);
        }
        out.flush();

        // 关闭流资源
        fis.close();
        out.close();
        // 删除服务器端生成的下载文件,减轻服务器的压力
        if (file.exists()) {
            if(!file.delete()) {
                throw new BusinessException("文件删除失败");
            }
        }
    }


    /*public static String stringFormat(String target, Object... source) {
        StringExpression expression = StringFormatter.format(target, source);
        return expression.getValue();
    }*/

    public static Date dateFormat(String dateStr) {

        SimpleDateFormat mt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return mt.parse(dateStr);
        } catch (ParseException e) {
            log.error("dateFormat error:", e);
        }
        return null;
    }


    /**
     * 参数转换，将"1,2,3"转换为[1,2,3]集合
     *
     * @param str
     * @param type
     * @return
     */
    public static List convertStringToList(String str, Class type) {

        List<? super Object> parameters = new ArrayList<>();

        String[] tmps = str.trim().split(",");

        String typeName = type.getSimpleName();

        switch (typeName) {
            case "Byte":
                for (String item : tmps) {
                    try {
                        Byte var1 = Byte.parseByte(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
                break;
            case "Long":
                for (String item : tmps) {
                    try {
                        Long var1 = Long.parseLong(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            default:
                for (String item : tmps) {
                    parameters.add(item);
                }
        }

        return parameters;
    }


    public static Set convertStringToSet(String str, Class type) {

        Set<? super Object> parameters = new HashSet<>();

        String[] tmps = str.trim().split(",");

        String typeName = type.getSimpleName();

        switch (typeName) {
            case "Byte":
                for (String item : tmps) {
                    try {
                        Byte var1 = Byte.parseByte(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
                break;
            case "Long":
                for (String item : tmps) {
                    try {
                        Long var1 = Long.parseLong(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            default:
                for (String item : tmps) {
                    parameters.add(item);
                }
        }

        return parameters;
    }

    /**
     * Map转换为对象
     *
     * @param map
     * @param target
     * @return
     * @throws Exception
     */
    public static Object convertMapToObject(Map map, Class target) throws Exception {

        BeanInfo beanInfo = Introspector.getBeanInfo(target);

        // 获取所有属性
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

        Object obj = target.newInstance();

        for (PropertyDescriptor descriptor : descriptors) {

            // map中是否包含该属性
            if (map.containsKey(descriptor.getName())) {

                String propertySimpleName = descriptor.getPropertyType().getSimpleName();

                String key = descriptor.getName();

                if (propertySimpleName.equals("Byte") || propertySimpleName.equals("byte")) {
                    Byte byteValue = Byte.parseByte(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, byteValue);
                    continue;
                }

                if (propertySimpleName.equals("Long") || propertySimpleName.equals("long")) {
                    Long longValue = Long.parseLong(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, longValue);
                    continue;
                }

                if (propertySimpleName.equals("Short") || propertySimpleName.equals("short")) {
                    Short shortValue = Short.parseShort(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, shortValue);
                    continue;
                }

                if (propertySimpleName.equals("Double") || propertySimpleName.equals("double")) {
                    Double doubleValue = Double.parseDouble(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, doubleValue);
                    continue;
                }

                if (propertySimpleName.equals("Float") || propertySimpleName.equals("float")) {
                    Float floatValue = Float.parseFloat(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, floatValue);
                    continue;
                }

                // 如果不符合上述条件
                Object value = map.get(key);
                descriptor.getWriteMethod().invoke(obj, value);

            }

        }

        return obj;
    }

    /**
     * 将source中的属性值赋给target中属性；
     * 如果source中的属性值不为空就进行赋值操作
     *
     * @param source
     * @param target 目标赋值类
     * @return
     * @throws Exception
     */
    public static Object copy(Object source, Object target) throws Exception {

        // 获取对象的说明信息
        BeanInfo sf = Introspector.getBeanInfo(source.getClass());
        BeanInfo tf = Introspector.getBeanInfo(target.getClass());

        // 获取属性说明信息，其中包括 属性名称，类型，属性的读写方法
        PropertyDescriptor[] desSf = sf.getPropertyDescriptors();
        PropertyDescriptor[] desTf = tf.getPropertyDescriptors();

        for (PropertyDescriptor var1 : desSf) {
            // 如果该属性名是class 并且类型为Class 则跳过
            if (var1.getName().equals("class") && var1.getPropertyType().getSimpleName().equals("Class")) {
                continue;
            }

            for (PropertyDescriptor var2 : desTf) {

                // 如果该属性名是class 并且类型为Class 则跳过
                if (var1.getName().equals("class") && var2.getPropertyType().getSimpleName().equals("Class")) {
                    continue;
                }

                if (var1.getName().equals(var2.getName())) {

                    Method methodSf = var1.getReadMethod();
                    Object obj = methodSf.invoke(source);
                    // 如果该属性的读方法返回的值不为空则进行赋值
                    if (obj != null) {
                        Method methodTf = var2.getWriteMethod();
                        // 反射 调用target 对应属性的写方法
                        methodTf.invoke(target, obj);
                    }

                }
            }
        }
        return target;
    }

    /**
     * 简单判断是否为手机号
     *
     * @param phoneNo 手机号
     * @return boolean
     */
    public static boolean isPhoneNo(String phoneNo) {
        String regex = "[1]\\d{10}";
        if (StringUtils.isBlank(phoneNo)) {
            return false;
        } else {
            return phoneNo.matches(regex);
        }
    }

    /**
     * 判断是否为 AJAX 请求
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null
            && "XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
    }

    /**
     * 由于security不支持json，所以要修改成formdata的方式来提交
     * @param request
     * @return
     */
    public static HttpServletRequest convertJsonType2FormData(HttpServletRequest request) throws ServletException, IOException {
        if (StringUtils.contains(request.getContentType(), "application/json")
            && Objects.equals( request.getServletPath(), LOGIN_PROCESSING_URL)) {

            BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            if(StringUtils.isBlank(responseStrBuilder.toString())){
                return request;
            }
            Map<String, String> map = JSON.parseObject(responseStrBuilder.toString(), new TypeReference<Map<String, String>>() {});
            Map<String, String[]> convertMap = new HashMap<String, String[]>();

            for (String key : map.keySet()) {
                String value = map.get(key);
                String[] convertMapValue = new String[]{value};
                convertMap.put(key,convertMapValue);
            }

            HttpServletRequest s = new MyServletRequestWrapper(((HttpServletRequest) request), convertMap);
            return s;
        } else {
            return request;
        }
    }

    /**
     * 获取request
     * @return
     */
    public static HttpServletRequest getRequest(){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return request;
    }

}

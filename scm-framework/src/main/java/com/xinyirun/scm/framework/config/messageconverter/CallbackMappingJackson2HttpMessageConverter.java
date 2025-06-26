package com.xinyirun.scm.framework.config.messageconverter;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.common.constant.JsonResultTypeConstants;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*public class CallbackMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    // 做jsonp的支持的标识，在请求参数中加该参数
    private String callbackName;


    /*
     * 注意 是覆盖有三个参数的方法
     * writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage)
     * @param object
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException

    @Override
    protected void writeInternal(Object object, Type type , HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        // 从threadLocal中获取当前的Request对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        String callbackParam = request.getParameter(callbackName);
        if (StringUtils.isEmpty(callbackParam)) {
            try {
                System.out.println(JSON.toJSONString(object));
                super.writeInternal(object, type, outputMessage);
            } catch (Exception ex) {
                throw new HttpMessageNotWritableException("转换json出错: " + ex.getMessage(), ex);
            }
        } else {
            JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
            try {
                String result = callbackParam + "(" + super.getObjectMapper().writeValueAsString(object)
                        + ");";
                IOUtils.write(result, outputMessage.getBody(), encoding.getJavaName());
            } catch (JsonProcessingException ex) {
                throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
            }
        }

    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }*/

/**
 * @author zhangxh
 */
public class CallbackMappingJackson2HttpMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
//        System.out.println(JSON.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect));
        // 中文乱码解决方案
        List<MediaType> mediaTypes = new ArrayList<>();
        //设定json格式且编码为UTF-8
//        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypes.add(MediaType.APPLICATION_JSON);
//        mediaTypes.add(MediaType.MULTIPART_FORM_DATA);
//        mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
//        mediaTypes.add(MediaType.valueOf("text/html;charset=UTF-8"));
        super.setSupportedMediaTypes(mediaTypes);

        /**
         *
         * QuoteFieldNames———-输出key时是否使用双引号,默认为true
         * WriteMapNullValue——–是否输出值为null的字段,默认为false
         * WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
         * WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
         * WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
         * WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
         *
         * */
        JsonResultAo jsonResultAo = null;
        try {
            jsonResultAo = (JsonResultAo)o;
        } catch (Exception e) {
        }
        if(jsonResultAo !=null){
            switch (jsonResultAo.getJson_result_type()) {
                case JsonResultTypeConstants.STRING_EMPTY_BOOLEAN_FALSE:
//                    super.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
//                            SerializerFeature.WriteNullStringAsEmpty,
//                            // SerializerFeature.WriteNullListAsEmpty,
//                            SerializerFeature.WriteNullBooleanAsFalse,
//                            SerializerFeature.PrettyFormat
//                    );
                    super.getFastJsonConfig().setWriterFeatures(JSONWriter.Feature.ReferenceDetection,
                            JSONWriter.Feature.WriteNullStringAsEmpty,
                            JSONWriter.Feature.WriteNullBooleanAsFalse,
                            JSONWriter.Feature.PrettyFormat
                    );
                    break;
                case JsonResultTypeConstants.NULL_NOT_OUT:
//                    super.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
//                            SerializerFeature.PrettyFormat
//                    );
                    super.getFastJsonConfig().setWriterFeatures(JSONWriter.Feature.ReferenceDetection,
                            JSONWriter.Feature.PrettyFormat
                    );
                    break;
                default:
//                    super.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
//                            SerializerFeature.WriteMapNullValue,
//                            //SerializerFeature.WriteNullStringAsEmpty,  --->注意这个不能开启
//                            //SerializerFeature.WriteNullListAsEmpty,    --->如果开启，json数组这里会自动生成一个空数组，而不是null
//                            SerializerFeature.WriteNullBooleanAsFalse,
//                            SerializerFeature.PrettyFormat
//                            //,
//                            // SerializerFeature.WriteNullNumberAsZero
//                    );
                    super.getFastJsonConfig().setWriterFeatures(JSONWriter.Feature.ReferenceDetection,
                            JSONWriter.Feature.WriteMapNullValue,
                            //SerializerFeature.WriteNullStringAsEmpty,  --->注意这个不能开启
                            //SerializerFeature.WriteNullListAsEmpty,    --->如果开启，json数组这里会自动生成一个空数组，而不是null
                            JSONWriter.Feature.WriteNullBooleanAsFalse,
                            JSONWriter.Feature.PrettyFormat
                            //,
                            // SerializerFeature.WriteNullNumberAsZero
                    );
                    break;
            }
        }


//        if(jsonResult != null && jsonResult.isJson_null_out() ){
//            super.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
//                SerializerFeature.WriteNullStringAsEmpty,
//                //                                                            SerializerFeature.WriteNullListAsEmpty,
//                SerializerFeature.WriteNullBooleanAsFalse,
//                SerializerFeature.PrettyFormat
//            );
//        } else {
//            super.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
//                SerializerFeature.WriteMapNullValue,
//                //                                                            SerializerFeature.WriteNullStringAsEmpty,  --->注意这个不能开启
//                SerializerFeature.WriteNullListAsEmpty,
//                SerializerFeature.WriteNullBooleanAsFalse,
//                SerializerFeature.PrettyFormat
//                //,
//                // SerializerFeature.WriteNullNumberAsZero
//            );
//        }
        super.write(o, type, contentType, outputMessage);
    }

}
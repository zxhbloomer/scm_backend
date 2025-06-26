package com.xinyirun.scm.framework.config.resttemplate;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.xinyirun.scm.framework.config.messageconverter.CallbackMappingJackson2HttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhangxh
 */
@Configuration
public class RestTemplateConfiguration {

    @Value("${RestTemplate-ConnectTimeout}")
    private int RestTemplate_ConnectTimeout;
    @Value("${RestTemplate-ReadTimeout}")
    private int RestTemplate_ReadTimeout;

//    @Autowired
//    private RestTemplateBuilder builder;

    @Bean
    @ConditionalOnMissingBean({RestTemplate.class})
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        //return builder.build();
        RestTemplate restTemplate = new RestTemplate(factory);

        //换上fastjson
        List<HttpMessageConverter<?>> httpMessageConverterList= restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator=httpMessageConverterList.iterator();
        while(iterator.hasNext()){
            HttpMessageConverter<?> converter=iterator.next();
            //原有的String是ISO-8859-1编码 去掉
            if(converter instanceof StringHttpMessageConverter){
                iterator.remove();
            }

            //由于系统中默认有jackson 在转换json时自动会启用  但是我们不想使用它 可以直接移除或者将fastjson放在首位
            /*if(converter instanceof GsonHttpMessageConverter || converter instanceof MappingJackson2HttpMessageConverter){
                iterator.remove();
            }*/
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);;
            }
        }
        CallbackMappingJackson2HttpMessageConverter fastJsonConvert = new CallbackMappingJackson2HttpMessageConverter();
        List<MediaType> supportMediaTypeList = new ArrayList<>();
        supportMediaTypeList.add(MediaType.APPLICATION_JSON);
//        supportMediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        supportMediaTypeList.add(MediaType.valueOf("application/*+json"));
        supportMediaTypeList.add(MediaType.valueOf("application/*+json;charset=UTF-8"));
        fastJsonConvert.setSupportedMediaTypes(supportMediaTypeList);

        httpMessageConverterList.add(new StringHttpMessageConverter(Charset.forName("utf-8")));
        httpMessageConverterList.add(0,fastJsonConvert);

        return restTemplate;

    }

    /**
     *@Author:Fly Created in 2018/6/23 下午3:05
     *@Description: 解决Spring默认处理返回Json无法解析Object问题，
     * 并且手动配置Jackson序列化LocalDateTime的时间格式，如果不手动
     * 配置的话，@JsonFormat会因为设置SerializationFeature.FAIL_ON_EMPTY_BEANS
     * 而失效，配置后无需再DTO中添加@JsonFormat(pattern = "yyyy-MM-dd HH:ss:mm", timezone = "GMT+8")注解
     */
//    @Bean
//    public ObjectMapper objectMapper(){
//
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//
//        javaTimeModule.addSerializer(
//                LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        return new ObjectMapper()
//                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                .registerModule(javaTimeModule);
//    }

    @Bean
    @ConditionalOnMissingBean({ClientHttpRequestFactory.class})
    public ClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //ms
        factory.setConnectTimeout(RestTemplate_ConnectTimeout);
        factory.setReadTimeout(RestTemplate_ReadTimeout);
        return factory;
    }
}

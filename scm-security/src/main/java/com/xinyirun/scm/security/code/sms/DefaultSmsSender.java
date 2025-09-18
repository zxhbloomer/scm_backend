package com.xinyirun.scm.security.code.sms;

import com.xinyirun.scm.bean.entity.sys.SSmsCodeEntity;
import com.xinyirun.scm.core.system.service.sys.ISSmsCodeService;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 短信发送
 */
@Slf4j
@Component
public class DefaultSmsSender implements SmsCodeSender {

    @Autowired
    ISSmsCodeService isSmsCodeService;

    @Autowired
    private SystemSecurityProperties systemSecurityProperties;

    @Value("${scm.security.code.sms.expire-in}")
    private long expireIn;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void sendCode(String mobile, String code, String type) throws Exception {
        String apiUri = systemSecurityProperties.getCode().getSms().getApiUri();
        String account = systemSecurityProperties.getCode().getSms().getAccount();
        String pswd = systemSecurityProperties.getCode().getSms().getPswd();
        /** 是否需要状态报告，需要true，不需要false */
        boolean needstatus = true;
        /** 扩展码 */
        String extno = null;

        /** code保存到数据库 */
        SSmsCodeEntity sSmsCodeEntity = new SSmsCodeEntity();
        sSmsCodeEntity.setMobile(mobile);
        sSmsCodeEntity.setCode(code);
        sSmsCodeEntity.setType(type);
        sSmsCodeEntity.setC_id(null);
        sSmsCodeEntity.setC_time(LocalDateTime.now());
        sSmsCodeEntity.setU_id(null);
        sSmsCodeEntity.setU_time(LocalDateTime.now());
        isSmsCodeService.save(sSmsCodeEntity);

        this.batchSend(apiUri, account, pswd, mobile, code, needstatus, extno);
        log.debug("手机号：" + mobile + "的短信验证码为：" + code + "，有效时间：" + expireIn + " 秒");
    }

    /**
     * 发送短信方法
     * @param url
     * @param account
     * @param pswd
     * @param mobile
     * @param msg
     * @param needstatus
     * @param extno
     * @throws Exception
     */
    public void batchSend(String url, String account, String pswd, String mobile, String msg, boolean needstatus,
        String extno) throws Exception {
        // 上传的url
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("account", account);
        param.add("pswd", pswd);
        param.add("mobile", mobile);
        param.add("needstatus", String.valueOf(needstatus));
        param.add("msg", msg);
        param.add("extno", extno);
        /**
         * request 头信息
         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(param, headers);

        //        restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);

        /**
         * 如果是develop模式，则不需要考虑验证码，直接跳出
         */
        if (systemSecurityProperties.getDevelopModel()){
            return;
        }

        /** 使用RestTemplate提供的方法创建RequestCallback */
        RequestCallback requestCallback = restTemplate.httpEntityCallback(httpEntity);
        /** 自定义返回值处理器 */
        ResponseExtractor responseExtractor = new ResponseExtractor() {
            @Override
            public Object extractData(ClientHttpResponse response) throws IOException {
                if(response.getStatusCode() == HttpStatus.OK){
                    log.debug("短信验证码发送成功【mobile("+ mobile +")】，" +"验证码为：" +  msg);
                } else {
                    log.debug("短信验证码发送失败【mobile("+ mobile +")】，" +"验证码为：" +  msg);
                }
                return null;
            }
        };
        Object rtn = restTemplate.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }
}

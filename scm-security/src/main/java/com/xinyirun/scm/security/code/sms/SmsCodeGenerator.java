package com.xinyirun.scm.security.code.sms;


import com.xinyirun.scm.security.code.ValidateCode;
import com.xinyirun.scm.security.code.ValidateCodeGenerator;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("smsCodeGenerator")
public class SmsCodeGenerator implements ValidateCodeGenerator {

    @Autowired
    private SystemSecurityProperties systemSecurityProperties;

    @Override
    public ValidateCode createCode() {
        String code = RandomStringUtils.randomNumeric(systemSecurityProperties.getCode().getSms().getLength());
        return new ValidateCode(code, systemSecurityProperties.getCode().getSms().getExpireIn());
    }
}

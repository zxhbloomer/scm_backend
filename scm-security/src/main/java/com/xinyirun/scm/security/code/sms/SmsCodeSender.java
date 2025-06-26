package com.xinyirun.scm.security.code.sms;

public interface SmsCodeSender {
    void sendCode(String mobile, String code, String type) throws Exception;
}

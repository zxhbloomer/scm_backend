package com.xinyirun.scm.security.code.img;

import com.google.code.kaptcha.Producer;
import com.xinyirun.scm.security.code.ValidateCodeGenerator;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;

public class ImageCodeGenerator implements ValidateCodeGenerator {

    private SystemSecurityProperties systemSecurityProperties;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Override
    public ImageCode createCode() {
        // 验证码有效时间
        int expireIn = systemSecurityProperties.getCode().getImage().getExpireIn();

        String capStr = null;
        String code = null;
        BufferedImage bi = null;

        if ("math".equals(systemSecurityProperties.getCode().getImage().getCaptchaType())) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            bi = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(systemSecurityProperties.getCode().getImage().getCaptchaType())) {
            capStr = code = captchaProducer.createText();
            bi = captchaProducer.createImage(capStr);
        }

        return new ImageCode(bi, code, expireIn);
    }

    public void setWmsSecurityProperties(SystemSecurityProperties systemSecurityProperties) {
        this.systemSecurityProperties = systemSecurityProperties;
    }
}

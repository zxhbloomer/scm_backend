package com.xinyirun.scm.controller.code.image;

import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.security.code.ValidateCodeGenerator;
import com.xinyirun.scm.security.code.img.ImageCode;
import com.xinyirun.scm.security.code.sms.SmsCodeSender;
// import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 图片验证码（支持算术形式）,
 *
 * @author
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
// @Api(tags = "图片验证码（支持算术形式）")
public class SysCaptchaController extends SystemBaseController {

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Autowired
    private ValidateCodeGenerator imageCodeGenerator;

    @Autowired
    private ValidateCodeGenerator smsCodeGenerator;

    @Autowired
    private SmsCodeSender smsCodeSender;


    /**
     * 验证码生成
     */
    @GetMapping(value = "/imagecode")
    public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = (ImageCode) imageCodeGenerator.createCode();
        BufferedImage image = imageCode.getImage();
        imageCode.setImage(null);
        sessionStrategy.setAttribute(new ServletWebRequest(request), SystemConstants.SESSION_KEY_IMAGE_CODE, imageCode);
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }
}
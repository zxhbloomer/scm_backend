package com.xinyirun.scm.core.whapp.service.business.wechat;

import com.xinyirun.scm.bean.whapp.vo.business.wechat.WeChatLoginVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * <p>
 * wx
 * </p>
 */
public interface WeChatService {


    /**
     * 获取accessToken,该步骤返回的accessToken期限为一个月
     */
    public String login(String code, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 绑定微信
     * @param code
     */
    public void bingWechat(String code);

    /**
     * 解绑微信
     */
    public void unbingWechat();


}

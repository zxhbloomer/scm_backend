package com.xinyirun.scm.security.handler.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import com.xinyirun.scm.core.app.service.master.user.jwt.AppIMUserJwtTokenService;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.framework.base.controller.app.v1.AppBaseController;
import com.xinyirun.scm.security.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(value = "jwtAuthenticationSuccessHandler")
public class JWTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;

    @Autowired
    private IMUserService userService;
    @Autowired
    private AppIMUserJwtTokenService imUserJwATokenService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    AppBaseController appBaseController;

    @Autowired
    public JWTAuthenticationSuccessHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = ((User) authentication.getPrincipal()).getUsername();

        MUserVo user = userService.getDataByName(username);

        // 设置 jwt user bean
        AppJwtBaseBo bean = appBaseController.getAppLoginBean(user.getId(), SystemConstants.LOGINUSER_OR_STAFF_ID.LOGIN_USER_ID);
        if (user.getIs_enable()) {
            AppMUserJwtTokenVo jwtTokenVo = tokenProvider.createToken(authentication, bean, true);
            AppMUserJwtTokenVo rtnVo = imUserJwATokenService.saveToken(jwtTokenVo);

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            //登录成功 记录最新登录时间
            userService.updateLoginDate(user.getId());

            mapper.writeValue(response.getWriter(),
                    ResultUtil.OK(jwtTokenVo.getToken(), ResultEnum.OK));
        } else {
            user = null;

            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getWriter(),
                    ResultUtil.NG(
                            HttpStatus.NOT_ACCEPTABLE.value(),
                            ResultEnum.JWT_AUTHENTICATION_NOT_ACTIVATED,
                            new JWTAuthException("用户尚未激活！"),
                            "用户尚未激活！",
                            request
                    ));
        }

    }

}

package com.xinyirun.scm.security.code.sms;

import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private static final String MOBILE_KEY = "mobile";

    private String mobileParameter = MOBILE_KEY;
    private boolean postOnly = true;

    public SmsCodeAuthenticationFilter(SystemSecurityProperties systemSecurityProperties) {
        super(new AntPathRequestMatcher(systemSecurityProperties.getCode().getSms().getLoginProcessingUrl(), HttpMethod.POST.toString()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.toString())) {
            throw new AuthenticationServiceException("认证方法不支持: " + request.getMethod());
        }

        String mobile = obtainMobile(request);

        if (mobile == null) {
            mobile = "";
        }

        mobile = mobile.trim();

        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    private void setDetails(HttpServletRequest request,
                            SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "手机号不能为空");
        this.mobileParameter = mobileParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getMobileParameter() {
        return mobileParameter;
    }
}

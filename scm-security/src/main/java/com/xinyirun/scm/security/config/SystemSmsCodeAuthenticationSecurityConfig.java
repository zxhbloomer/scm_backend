package com.xinyirun.scm.security.config;

import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.security.code.sms.SmsCodeAuthenticationFilter;
import com.xinyirun.scm.security.code.sms.SmsCodeAuthenticationProvider;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信认证流程配置
 * @author zxh
 */
@Component
public class SystemSmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    @Qualifier("systemAuthenticationSucessHandler")
    private AuthenticationSuccessHandler systemAuthenticationSucessHandler;

    @Qualifier("systemAuthenticationFailureHandler")
    @Autowired
    private AuthenticationFailureHandler systemAuthenticationFailureHandler;

    @Autowired
    private SystemSecurityProperties systemSecurityProperties;

    @Autowired
    private IMUserService userDetailService;

    @Override
    public void configure(HttpSecurity http) {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter(
                systemSecurityProperties);
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(systemAuthenticationSucessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(systemAuthenticationFailureHandler);

        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailService(userDetailService);

        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }
}

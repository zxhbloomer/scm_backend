package com.xinyirun.scm.security.config;

import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserService;
import com.xinyirun.scm.core.app.service.master.user.jwt.AppIMUserJwtTokenService;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.core.system.service.sys.app.token.ISTokenService;
import com.xinyirun.scm.security.code.ValidateCodeGenerator;
import com.xinyirun.scm.security.code.img.ImageCodeFilter;
import com.xinyirun.scm.security.code.img.ImageCodeGenerator;
import com.xinyirun.scm.security.code.sms.DefaultSmsSender;
import com.xinyirun.scm.security.code.sms.SmsCodeFilter;
import com.xinyirun.scm.security.code.sms.SmsCodeSender;
import com.xinyirun.scm.security.cors.CorsFilter;
import com.xinyirun.scm.security.handler.jwt.JWTAccessDeniedHandler;
import com.xinyirun.scm.security.handler.jwt.JWTAuthenticationFailureHandler;
import com.xinyirun.scm.security.handler.system.SystemAuthenticationAccessDeniedHandler;
import com.xinyirun.scm.security.handler.system.SystemAuthenticationFailureHandler;
import com.xinyirun.scm.security.handler.system.SystemLogoutHandler;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import com.xinyirun.scm.security.requestid.ApiRequestIdFilter;
import com.xinyirun.scm.security.requestid.AppRequestIdFilter;
import com.xinyirun.scm.security.requestid.SystemRequestIdFilter;
import com.xinyirun.scm.security.security.SsoAuthenticationManager;
import com.xinyirun.scm.security.security.UsernamePasswordAuthenticationManager;
import com.xinyirun.scm.security.security.api.ResultExceptionTranslationFilter;
import com.xinyirun.scm.security.security.api.TokenAuthenticationFilter;
import com.xinyirun.scm.security.security.api.TokenAuthenticationProvider;
import com.xinyirun.scm.security.security.jwt.*;
import com.xinyirun.scm.security.security.sso.SsoAuthenticationFailureHandler;
import com.xinyirun.scm.security.security.sso.SsoAuthenticationProvider;
import com.xinyirun.scm.security.security.sso.SsoLoginFilter;
import com.xinyirun.scm.security.session.SystemExpiredSessionStrategy;
import com.xinyirun.scm.security.session.SystemInvalidSessionStrategy;
import com.xinyirun.scm.security.xss.XssFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "server.security.enabled", havingValue = "true", matchIfMissing = true)
@EnableWebSecurity
public class SystemSecurityConfig {

    /**
     * 用户名 密码方式
     */
    @Order(1)
    @Configuration
    @ConditionalOnProperty(name = "server.security.enabled", havingValue = "true", matchIfMissing = true)
    @ComponentScan({"com.xinyirun.scm.core","com.xinyirun.scm.security","com.xinyirun.scm.framework"})
    public class SecurityUserPasswordConfig {

        private static final String PATTERN_SQUARE = "/api/v1/**";

        private AuthenticationManager authenticationManager;

        /**
         * 登录成功处理器
         */
        @Autowired
        @Qualifier("systemAuthenticationSucessHandler")
        private AuthenticationSuccessHandler systemAuthenticationSucessHandler;

        @Autowired
        private IMUserService userDetailService;

        /**
         * 登录失败处理器
         */
        @Autowired
        private SystemAuthenticationFailureHandler systemAuthenticationFailureHandler;

        @Autowired
        private SystemSecurityProperties systemSecurityProperties;

        @Autowired
        private SystemSmsCodeAuthenticationSecurityConfig systemSmsCodeAuthenticationSecurityConfig;

//        @Qualifier("master")
        @Autowired
        private DataSource dataSource;

        /**
         * 权限前缀
         *
         * @return
         */
        @Bean
        GrantedAuthorityDefaults grantedAuthorityDefaults() {
            // Remove the ROLE_ prefix
            return new GrantedAuthorityDefaults("");
        }


        /**
         * spring security自带的密码加密工具类
         *
         * @return
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        /**
         * 处理 rememberMe 自动登录认证
         *
         * @return
         */
        @Bean
        public PersistentTokenRepository persistentTokenRepository() {
            JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
            jdbcTokenRepository.setDataSource(dataSource);
            jdbcTokenRepository.setCreateTableOnStartup(false);
            return jdbcTokenRepository;
        }

        @Bean
        public SecurityFilterChain UserPasswordFilterChain(HttpSecurity http) throws Exception {
            String[] anonResourcesUrl = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                    systemSecurityProperties.getAnonResourcesUrl(), ",");

            ImageCodeFilter imageCodeFilter = new ImageCodeFilter();
            imageCodeFilter.setAuthenticationFailureHandler(systemAuthenticationFailureHandler);
            imageCodeFilter.setWmsSecurityProperties(systemSecurityProperties);
            imageCodeFilter.afterPropertiesSet();

            SmsCodeFilter smsCodeFilter = new SmsCodeFilter();
            smsCodeFilter.setAuthenticationFailureHandler(systemAuthenticationFailureHandler);
            smsCodeFilter.setWmsSecurityProperties(systemSecurityProperties);
            smsCodeFilter.setSessionRegistry(sessionRegistry());
            smsCodeFilter.afterPropertiesSet();

            // 此处说明按这个路径来匹配安全策略
            return http.securityMatcher(PATTERN_SQUARE)
                    .addFilterBefore(new SystemRequestIdFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
                    /** 短信验证码校验 */
                    .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class)
                    /** 添加图形证码校验过滤器 */
                    .addFilterBefore(imageCodeFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                            //任何人都可以访问
                            .requestMatchers(anonResourcesUrl).permitAll()
                            .requestMatchers(
                                    /** 登录路径 */
                                    systemSecurityProperties.getLoginUrl(),
                                    /** 用户注册 url */
                                    SystemConstants.FEBS_REGIST_URL,
                                    /** 创建图片验证码路径 */
                                    systemSecurityProperties.getCode().getImage().getCreateUrl(),
                                    /** 创建短信验证码路径 */
                                    systemSecurityProperties.getCode().getSms().getCreateUrl()
                            ).permitAll()
                            /** 其他请求  都需要认证 */
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session
                            /** 处理 session失效 */
                            .invalidSessionStrategy(invalidSessionStrategy())
                            /** 最大并发登录数量 */
                            .maximumSessions(systemSecurityProperties.getMAX_SESSIONS())
                            // 当达到最大值时，是否保留已经登录的用户
                            .maxSessionsPreventsLogin(false)
                            /** 处理并发登录被踢出 */
                            .expiredSessionStrategy(new SystemExpiredSessionStrategy())
                            /** 配置 session注册中心 */
                            .sessionRegistry(sessionRegistry())
                    )
                    .formLogin(login -> login
                            /** 未认证跳转 URL */
                            .loginPage(systemSecurityProperties.getLoginUrl())
                            .usernameParameter("username").passwordParameter("password")
                            /** 处理登录认证 URL */
                            .loginProcessingUrl(systemSecurityProperties.getCode().getImage().getLoginProcessingUrl())
                            /** 处理登录成功 */
                            .successHandler(systemAuthenticationSucessHandler)
                            /** 处理登录失败 */
                            .failureHandler(systemAuthenticationFailureHandler)
                    )
                    /** 添加记住我功能 */
                    .rememberMe(rememberMe -> rememberMe
                            .userDetailsService(userDetailService)
                            /** 配置 token 持久化仓库 */
                            .tokenRepository(persistentTokenRepository())
                            /** rememberMe 过期时间，单为秒 */
                            .tokenValiditySeconds(systemSecurityProperties.getRememberMeTimeout())
                    )
                    .exceptionHandling(handle -> handle
                            /** 权限不足处理器 */
                            .accessDeniedHandler(accessDeniedHandler())
                    )
                    .logout(logout -> logout
                            /** 配置登出处理器 */
                            .addLogoutHandler(logoutHandler())
                            /** 处理登出 url */
                            //                .logoutUrl(systemSecurityProperties.getLogoutUrl())
                            /** 登出后跳转到 */
                            //                .logoutSuccessUrl("/")
                            /** 删除 JSESSIONID */
                            .deleteCookies("JSESSIONID")
                    )
                    .csrf(csrf -> {
                                try {
                                    csrf
                                            .disable()
                                            /** 添加短信验证码认证流程 */
                                            .apply(systemSmsCodeAuthenticationSecurityConfig);
                                } catch (Exception e) {
                                    throw new BusinessException(e);
                                }
                            }
                    )
                    .authenticationProvider(userPasswordAuthenticationProvider())
                    .build();
        }

        @Bean
        public DaoAuthenticationProvider userPasswordAuthenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

            authProvider.setUserDetailsService(userDetailService);
            authProvider.setPasswordEncoder(passwordEncoder());

            return authProvider;
        }


        @Bean
        @ConditionalOnMissingBean(name = "imageCodeGenerator")
        public ValidateCodeGenerator imageCodeGenerator() {
            ImageCodeGenerator imageCodeGenerator = new ImageCodeGenerator();
            imageCodeGenerator.setWmsSecurityProperties(systemSecurityProperties);
            return imageCodeGenerator;
        }

        @Bean
        @ConditionalOnMissingBean(SmsCodeSender.class)
        public SmsCodeSender smsCodeSender() {
            return new DefaultSmsSender();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        /**
         * 配置登出处理器
         *
         * @return
         */
        @Bean
        public LogoutHandler logoutHandler() {
            SystemLogoutHandler systemLogoutHandler = new SystemLogoutHandler();
            systemLogoutHandler.setSessionRegistry(sessionRegistry());
            return systemLogoutHandler;
        }

        @Bean
        public InvalidSessionStrategy invalidSessionStrategy() {
            SystemInvalidSessionStrategy systemInvalidSessionStrategy = new SystemInvalidSessionStrategy();
            systemInvalidSessionStrategy.setWmsSecurityProperties(systemSecurityProperties);
            return systemInvalidSessionStrategy;
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
            return new SystemAuthenticationAccessDeniedHandler();
        }

        /**
         * XssFilter Bean
         */
        @Bean
        @SuppressWarnings({"unchecked", "rawtypes"})
        public FilterRegistrationBean xssFilterRegistrationBean() {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            filterRegistrationBean.setFilter(new XssFilter());
            filterRegistrationBean.setOrder(1);
            filterRegistrationBean.setEnabled(true);
            filterRegistrationBean.addUrlPatterns("/*");
            Map<String, String> initParameters = new HashMap<>();
            initParameters.put("excludes", "/favicon.ico,/img/*,/js/*,/css/*");
            initParameters.put("isIncludeRichText", "true");
            filterRegistrationBean.setInitParameters(initParameters);
            return filterRegistrationBean;
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            String[] anonResourcesUrl = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                    systemSecurityProperties.getAnonResourcesUrl(), ",");
            return web -> web.ignoring().requestMatchers(anonResourcesUrl);
        }

    }

    /**
     * security api 配置中心
     * @author zxh
     */
    @Configuration
    @ConditionalOnProperty(name = "server.security.enabled", havingValue = "true", matchIfMissing = true)
    @Order(2)
    @ComponentScan({"com.xinyirun.scm.core","com.xinyirun.scm.security","com.xinyirun.scm.framework"})
    public class ApiSecurityConfig {

        private static final String PATTERN_SQUARE = "/api/service/**";

        @Autowired
        private ISTokenService tokenService;

//        @Qualifier("master")
        @Autowired
        private DataSource dataSource;

        @Bean
        public SecurityFilterChain ApiFilterChain(HttpSecurity http) throws Exception {
            // 此处说明按这个路径来匹配安全策略
            return http.securityMatcher(PATTERN_SQUARE)
                    .addFilterAfter(new ApiRequestIdFilter(), BasicAuthenticationFilter.class)
                    .addFilterAfter(new TokenAuthenticationFilter(), BasicAuthenticationFilter.class)
                    .addFilterAfter(new ResultExceptionTranslationFilter(), ExceptionTranslationFilter.class)
                    .authorizeRequests(authorizeRequests -> authorizeRequests
                            /** 其他请求  都需要认证 */
                            .anyRequest().authenticated()
                    )
                    .csrf(csrf -> csrf
                            .disable()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authenticationProvider(new TokenAuthenticationProvider(tokenService))
                    .build();
        }
    }


    /**
     * jwt 配置中心
     * @author zxh
     */
    @Configuration
    @ConditionalOnProperty(name = "server.security.enabled", havingValue = "true", matchIfMissing = true)
    @Order(3)
    @ComponentScan({"com.xinyirun.scm.core","com.xinyirun.scm.security","com.xinyirun.scm.framework"})
    public class JwtSecurityConfig {

        private static final String PATTERN_SQUARE = "/api/app/**";

        String[] anonResourcesUrl = {"/api/app/authenticate",
                "/api/app/oauth/authorize",
                "/api/app/oauth/token",
                "/api/app/me",
        };
        @Autowired
        private TokenProvider tokenProvider;
        @Autowired
        private CorsFilter corsFilter;
        @Autowired
        private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        @Autowired
        private JWTAuthenticationFailureHandler jwtAuthenticationFailureHandler;
        @Autowired
        private JWTAccessDeniedHandler jwtAccessDeniedHandler;
        @Autowired
        @Qualifier("jwtAuthenticationSuccessHandler")
        private AuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
        @Autowired
        private AppIMUserService userDetailService;
        @Autowired
        private AppIMUserJwtTokenService appIMUserJwtTokenServicel;
        @Value("${wms.security.jwt.base64-secret}")
        private String base64Secret;
        @Value("${spring.security.oauth2.client.provider.[custom].token-uri}")
        private String tokenUri;
        @Autowired
        private UsernamePasswordAuthenticationManager authenticationManager;

        /**
         * 登录认证器 --- 账号
         */
        public JWTAuthenticationFilter jwtLoginAuthenticationFilter() throws Exception {
            JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager,
                    jwtAuthenticationSuccessHandler,
                    jwtAuthenticationFailureHandler,
                    jwtAuthenticationEntryPoint);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/app/oauth/token");
            return jwtAuthenticationFilter;
        }
        
        /**
         * 有token时的校验
         * @return
         * @throws Exception
         */
        public JWTFilter jwtFilter() throws Exception {
            return new JWTFilter(tokenProvider, jwtAuthenticationFailureHandler, appIMUserJwtTokenServicel, base64Secret, tokenUri);
        }

        @Bean
        public SecurityFilterChain JwtFilterChain(HttpSecurity http) throws Exception {

            // 此处说明按这个路径来匹配安全策略
            return http.securityMatcher(PATTERN_SQUARE)
                    .addFilterBefore(new AppRequestIdFilter(), UsernamePasswordAuthenticationFilter.class)
                    // 有token时的过滤器
                    .addFilterAfter(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                    // login的过滤器
                    .addFilterAfter(jwtLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                            //任何人都可以访问
                            .requestMatchers(anonResourcesUrl).permitAll()
                            .requestMatchers("/api/app/authenticate").permitAll()
                            .requestMatchers("/api/app/oauth/authorize").permitAll()
                            .requestMatchers("/api/app/oauth/token").permitAll()
                            .requestMatchers("/api/app/me").permitAll()
                            /** 其他请求  都需要认证 */
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(handle -> handle
                            /** 权限不足处理器 */
                            .accessDeniedHandler(jwtAccessDeniedHandler)
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    )
                    .csrf(csrf -> csrf
                            .disable()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .build();
        }

    }


    /**
     * 只包含用户名、无密码 方式，为实现用户名（加密），sso登录
     */
    @Order(4)
    @Configuration
    @ConditionalOnProperty(name = "server.security.enabled", havingValue = "true", matchIfMissing = true)
    @ComponentScan({"com.xinyirun.scm.core","com.xinyirun.scm.security","com.xinyirun.scm.framework"})
    public class SecurityUserConfig {

        private static final String PATTERN_SQUARE = "/api/sso/v1/**";

        /**
         * 登录成功处理器
         */
        @Autowired
        @Qualifier("ssoAuthenticationSucessHandler")
        private AuthenticationSuccessHandler ssoAuthenticationSucessHandler;

        /**
         * 登录失败处理器
         */
        @Autowired
        @Qualifier("ssoAuthenticationFailureHandler")
        private SsoAuthenticationFailureHandler ssoAuthenticationFailureHandler;

        @Autowired
        private SystemSecurityProperties systemSecurityProperties;

        @Autowired
        private IMUserService userDetailService;

        @Autowired
        private SsoAuthenticationManager ssoAuthenticationManager;


//        @Qualifier("master")
        @Autowired
        private DataSource dataSource;

        @Bean
        public SsoAuthenticationProvider ssoAuthenticationProvider() {
            return new SsoAuthenticationProvider();
        }


        /**
         * 处理 rememberMe 自动登录认证
         *
         * @return
         */
        @Bean
        public PersistentTokenRepository persistentTokenRepository1() {
            JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
            jdbcTokenRepository.setDataSource(dataSource);
            jdbcTokenRepository.setCreateTableOnStartup(false);
            return jdbcTokenRepository;
        }

        /**
         * 登录认证器 --- 账号
         */
        @Bean
        public AbstractAuthenticationProcessingFilter ssoLoginFilter() throws Exception {
            SsoLoginFilter ssoLoginFilter = new SsoLoginFilter(new AntPathRequestMatcher("/api/sso/v1/user/username/token/get", "POST"));
            ssoLoginFilter.setAuthenticationManager(ssoAuthenticationManager);
            ssoLoginFilter.setAuthenticationSuccessHandler(ssoAuthenticationSucessHandler);
            ssoLoginFilter.setAuthenticationFailureHandler(ssoAuthenticationFailureHandler);
            return ssoLoginFilter;
        }

        @Bean
        public SecurityFilterChain OssFilterChain(HttpSecurity http) throws Exception {
            String[] anonResourcesUrl = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                    systemSecurityProperties.getAnonResourcesUrl(), ",");

            // 此处说明按这个路径来匹配安全策略
            return http.securityMatcher(PATTERN_SQUARE)
                    .addFilterBefore(ssoLoginFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                    .authorizeRequests(authorizeRequests -> authorizeRequests
                            .requestMatchers(anonResourcesUrl).permitAll()
                            .requestMatchers("/api/sso/v1/user/username/token/get").permitAll()
                            /** 其他请求  都需要认证 */
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(handle -> handle
                            /** 权限不足处理器 */
                            .accessDeniedHandler(accessDeniedHandler1())
                    )
                    .formLogin(login -> login
                            /** 未认证跳转 URL */
                            .loginPage(systemSecurityProperties.getLoginUrl())
                            .usernameParameter("username")
                            /** 处理登录认证 URL */
                            .loginProcessingUrl("/api/sso/v1/user/username/token/get")
                            /** 处理登录成功 */
                            .successHandler(ssoAuthenticationSucessHandler)
                            /** 处理登录失败 */
                            .failureHandler(ssoAuthenticationFailureHandler)
                    )
                    .sessionManagement(session -> session
                            /** 处理 session失效 */
                            .invalidSessionStrategy(invalidSessionStrategy1())
                            /** 最大并发登录数量 */
                            .maximumSessions(systemSecurityProperties.getMAX_SESSIONS())
                            // 当达到最大值时，是否保留已经登录的用户
                            .maxSessionsPreventsLogin(false)
                            /** 处理并发登录被踢出 */
                            .expiredSessionStrategy(new SystemExpiredSessionStrategy())
                            /** 配置 session注册中心 */
                            .sessionRegistry(sessionRegistry1())
                    )
                    .logout(logout -> logout
                            /** 配置登出处理器 */
                            .addLogoutHandler(logoutHandler1())
                            /** 处理登出 url */
                            //                .logoutUrl(systemSecurityProperties.getLogoutUrl())
                            /** 登出后跳转到 */
                            //                .logoutSuccessUrl("/")
                            /** 删除 JSESSIONID */
                            .deleteCookies("JSESSIONID")
                    )
//                    /** 添加记住我功能 */
//                    .rememberMe(rememberMe -> rememberMe
//                            /** 配置 token 持久化仓库 */
//                            .tokenRepository(persistentTokenRepository1())
//                            /** rememberMe 过期时间，单为秒 */
//                            .tokenValiditySeconds(systemSecurityProperties.getRememberMeTimeout())
//                    )
                    .csrf(csrf -> csrf
                            .disable()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authenticationProvider(ssoAuthenticationProvider())
                    .build();
        }

        @Bean
        public SessionRegistry sessionRegistry1() {
            return new SessionRegistryImpl();
        }

        /**
         * 配置登出处理器
         *
         * @return
         */
        @Bean
        public LogoutHandler logoutHandler1() {
            SystemLogoutHandler systemLogoutHandler = new SystemLogoutHandler();
            systemLogoutHandler.setSessionRegistry(sessionRegistry1());
            return systemLogoutHandler;
        }

        @Bean
        public InvalidSessionStrategy invalidSessionStrategy1() {
            SystemInvalidSessionStrategy systemInvalidSessionStrategy = new SystemInvalidSessionStrategy();
            systemInvalidSessionStrategy.setWmsSecurityProperties(systemSecurityProperties);
            return systemInvalidSessionStrategy;
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler1() {
            return new SystemAuthenticationAccessDeniedHandler();
        }

        /**
         * XssFilter Bean
         */
        @Bean
        @SuppressWarnings({"unchecked", "rawtypes"})
        public FilterRegistrationBean xssFilterRegistrationBean1() {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            filterRegistrationBean.setFilter(new XssFilter());
            filterRegistrationBean.setOrder(4);
            filterRegistrationBean.setEnabled(true);
            filterRegistrationBean.addUrlPatterns("/*");
            Map<String, String> initParameters = new HashMap<>();
            initParameters.put("excludes", "/favicon.ico,/img/*,/js/*,/css/*");
            initParameters.put("isIncludeRichText", "true");
            filterRegistrationBean.setInitParameters(initParameters);
            return filterRegistrationBean;
        }
    }

}

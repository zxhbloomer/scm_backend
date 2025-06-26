package com.xinyirun.scm.security.security.jwt;

import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import com.xinyirun.scm.core.app.service.master.user.jwt.AppIMUserJwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
@Slf4j
public class JWTFilter extends GenericFilterBean {

   private AppIMUserJwtTokenService appIMUserJwtTokenServicel;

   private String base64Secret;

   public static final String AUTHORIZATION_HEADER = "Authorization";

   private AuthenticationFailureHandler authenticationFailureHandler;

   private TokenProvider tokenProvider;

   private String tokenUri;

   public JWTFilter(TokenProvider tokenProvider,
                    AuthenticationFailureHandler authenticationFailureHandler,
                    AppIMUserJwtTokenService appIMUserJwtTokenServicel,
                    String base64Secret,
                    String tokenUril) {
      this.tokenProvider = tokenProvider;
      this.authenticationFailureHandler = authenticationFailureHandler;
      this.appIMUserJwtTokenServicel = appIMUserJwtTokenServicel;
      this.base64Secret = base64Secret;
      this.tokenUri = tokenUril;
   }

   /**
    * 此方法为，调用正常app请求时，httphead中包含授权信息，入口
    * @param servletRequest
    * @param servletResponse
    * @param filterChain
    * @throws IOException
    * @throws ServletException
    */
   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      String jwt = resolveToken(httpServletRequest);
      String requestURI = httpServletRequest.getRequestURI();

      try {
         if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("set Authentication to security context for '{}', uri: {}", authentication.getName(), requestURI);
         } else {
            log.debug("在request head中未找到合法的token, uri: {}", requestURI);
         }
      } catch (JWTAuthException e) {
         authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, (HttpServletResponse) servletResponse, e);
         return;
      }

      log.debug("颁发token接口的url为, tokenUri: {}", tokenUri);
      if (tokenUri.indexOf(requestURI) < 0) {
         /**
          * 此处进行check，关于jwt自定义的check
          * 1、m_user_jwt_token，中不存在
          * 2、m_user_jwt_token，中已经过期
          */
         try {
            appIMUserJwtTokenServicel.checkJWTToken(jwt, base64Secret);
         } catch (JWTAuthException e) {
            authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, (HttpServletResponse) servletResponse, e);
            return;
         }
      }

      filterChain.doFilter(servletRequest, servletResponse);
   }

   private String resolveToken(HttpServletRequest request) {
      String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
         return bearerToken.substring(7);
      }
      return null;
   }
}

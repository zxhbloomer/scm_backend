package com.xinyirun.scm.security.security.jwt;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import com.xinyirun.scm.bean.system.bo.user.api.ApiKeyAndSecretKeyBo;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean , AuthenticationProvider {

   private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

   private static final String AUTHORITIES_KEY = "auth";

   private final String base64Secret;
   private final long tokenValidityInMilliseconds;
//   private final long tokenValidityInMillisecondsForRememberMe;

   private Key key;

   public TokenProvider(
           @Value("${scm.security.jwt.base64-secret}") String base64Secret,
           @Value("${scm.security.jwt.token-validity-in-seconds}") long tokenValidityInSeconds
//           @Value("${scm.security.jwt.token-validity-in-seconds-for-remember-me}") long tokenValidityInSecondsForRememberMe
   ) {
      this.base64Secret = base64Secret;
      this.tokenValidityInMilliseconds = tokenValidityInSeconds ;
//      this.tokenValidityInMillisecondsForRememberMe = tokenValidityInSecondsForRememberMe ;
   }

   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   public AppMUserJwtTokenVo createToken(Authentication authentication, AppJwtBaseBo bean, boolean rememberMe) {
      String authorities = authentication.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(","));

      long now = (new Date()).getTime();
      Date validity;
//      if (rememberMe) {
//         validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
//      } else {
         validity = new Date(now + this.tokenValidityInMilliseconds);
//      }

      bean.setToken_expires_at(LocalDateTimeUtils.convertDateToLDT(validity));
      String token = Jwts.builder()
              .setSubject(JSON.toJSONString(bean))
              .claim(AUTHORITIES_KEY, authorities)
              .signWith(key, SignatureAlgorithm.HS256)
              .setExpiration(validity)
              .compact();

      AppMUserJwtTokenVo vo = new AppMUserJwtTokenVo();
      vo.setUser_id(bean.getUser_Id());
      vo.setStaff_id(bean.getStaff_Id());
      vo.setStaff_code(bean.getStaff_code());
      vo.setToken(token);
      vo.setToken_expires_at(LocalDateTimeUtils.convertDateToLDT(validity));
      vo.setLast_login_date(LocalDateTime.now());
      vo.setC_id(bean.getUser_Id());
      vo.setC_time( LocalDateTime.now());
      return vo;
   }

   public Authentication getAuthentication(String token) {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(key).build()
              .parseClaimsJws(token)
              .getBody();

      Collection<? extends GrantedAuthority> authorities =
              Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                      .map(SimpleGrantedAuthority::new)
                      .collect(Collectors.toList());

      User principal = new User(claims.getSubject(), "", authorities);

      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   public boolean validateToken(String authToken) {
      try {
         Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
         return true;
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         log.debug("JWT 签名无效。");
         throw new JWTAuthException("JWT 签名无效。",e);
      } catch (ExpiredJwtException e) {
         log.debug("过期的 JWT 令牌。");
         throw new JWTAuthException("过期的 JWT 令牌。",e);
      } catch (UnsupportedJwtException e) {
         log.debug("不支持的 JWT 令牌。");
         throw new JWTAuthException("不支持的 JWT 令牌。",e);
      } catch (IllegalArgumentException e) {
         log.debug("处理程序的JWT令牌压缩无效。");
         throw new JWTAuthException("处理程序的JWT令牌压缩无效。",e);
      }
//      return false;
   }

   @Override
   public Authentication authenticate(Authentication authentication)
           throws AuthenticationException {

      if (authentication.isAuthenticated()) {
         return authentication;
      }


      return null;
   }

   @Override
   public boolean supports(Class<?> authentication) {
      // 인증 객체가 JwtAuthenticationToken과 같을 때 authenticate 메소드 호출
      return JwtAuthenticationToken.class.isAssignableFrom(authentication);
   }

}

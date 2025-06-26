package com.xinyirun.scm.security.security.jwt;

import com.xinyirun.scm.common.constant.JWTSecurityConstants;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// For checking JWT Authentication at the time of hitting other apis
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private JWTAuthenticationEntryPoint entryPoint;

    public JWTAuthorizationFilter(AuthenticationManager authManager, JWTAuthenticationEntryPoint entryPoint) {
        super(authManager);
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(req, res);
    }
}

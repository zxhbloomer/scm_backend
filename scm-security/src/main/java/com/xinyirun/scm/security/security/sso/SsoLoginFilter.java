package com.xinyirun.scm.security.security.sso;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class SsoLoginFilter extends AbstractAuthenticationProcessingFilter {

    public SsoLoginFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        Map<String, String> authenticationBean = objectMapper.readValue(request.getInputStream(), Map.class);

        String username = authenticationBean.get("username");

        if (username == null) {
            username = "";
        }

        SsoCodeAuthenticationToken token = new SsoCodeAuthenticationToken(username);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);

        return super.getAuthenticationManager().authenticate(token);
    }
}

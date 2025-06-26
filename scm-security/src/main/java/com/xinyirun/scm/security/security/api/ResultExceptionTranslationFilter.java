package com.xinyirun.scm.security.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiAuthException;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * There might be ResultExceptions during authentication, this filter will transfer the exceptions
 * into 200 with JSON body explaining why.
 * <p>
 * Mainly used for Token Authentication.
 */

public class ResultExceptionTranslationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        try {
            fc.doFilter(request, response);
        } catch (ApiAuthException ex){
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(
                            ApiResultUtil.NG(
                                    ex.getEnumData(),
                                    ex.getMessage(),
                                    (HttpServletRequest) request)
                    )
            );
        } catch (Exception ex) {
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
//            response.getWriter().println(JsonUtil.toJson(Response.of(ex)));
            response.getWriter().write(objectMapper.writeValueAsString(
                            ApiResultUtil.NG(
                                    (ApiResultEnum) request.getAttribute("error.enum"),
                                    ex.getMessage(),
                                    (HttpServletRequest) request)
                    )
            );
            response.getWriter().flush();
        }
    }
}

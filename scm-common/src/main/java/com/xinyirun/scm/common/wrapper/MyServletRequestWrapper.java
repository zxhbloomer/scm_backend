package com.xinyirun.scm.common.wrapper;

import org.springframework.security.web.savedrequest.Enumerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;

public class MyServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> params;

    public MyServletRequestWrapper(HttpServletRequest request, Map<String, String[]> params) {
        super(request);
        this.params = params;
    }

    @Override
    public String getParameter(String name) {
        if (this.params.containsKey(name)) {
            return this.params.get(name)[0];
        }
        return "";
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Enumerator<>(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }
}

package com.xinyirun.scm.bpm.controller;

import com.xinyirun.scm.common.bpm.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Api(tags = "trigger", value = "template")
@RestController
@RequestMapping("/ex/trigger")
public class TriggerController {

//    @ApiOperation("Get请求调用")
    @GetMapping("getRequest")
    public Result getRequest(HttpServletRequest request) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name	= enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        log.error("请求的Headers:为{}",headerMap);
        Map<String, String> parameterMap = new HashMap<>();
        BufferedReader br = request.getReader();

        String str, wholeStr = "";
        while((str = br.readLine()) != null){
            wholeStr += str;
        }

        log.error("请求的Body:为{}",wholeStr);

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name	= parameterNames.nextElement();
            String value = request.getParameter(name);
            parameterMap.put(name, value);
        }
        log.error("请求的path参数为:为{}",parameterMap);

        return Result.OK();
    }

//    @ApiOperation("Post请求调用")
    @PostMapping("postRequest")
    public Result postRequest(HttpServletRequest request) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name	= enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        log.error("请求的Headers:为{}",headerMap);

        BufferedReader br = request.getReader();

        String str, wholeStr = "";
        while((str = br.readLine()) != null){
            wholeStr += str;
        }

        log.error("请求的Body:为{}",wholeStr);


        return Result.OK();
    }
}

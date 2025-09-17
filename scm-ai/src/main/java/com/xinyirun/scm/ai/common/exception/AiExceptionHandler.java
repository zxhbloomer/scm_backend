package com.xinyirun.scm.ai.common.exception;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.enums.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AiExceptionHandler {

    @ExceptionHandler(AiServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public JsonResultAo<String> handleAiServiceUnavailable(AiServiceUnavailableException e) {
        log.error("AI服务不可用: {}", e.getMessage());
        return ResultUtil.OK("AI服务暂时不可用，请稍后重试", ResultEnum.SYSTEM_BUSINESS_ERROR);
    }
}
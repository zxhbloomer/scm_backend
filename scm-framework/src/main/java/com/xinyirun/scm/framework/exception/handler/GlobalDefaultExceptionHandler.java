package com.xinyirun.scm.framework.exception.handler;

import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.fund.FundBusinessException;
import com.xinyirun.scm.common.exception.inventory.InventoryBusinessException;
import com.xinyirun.scm.common.exception.redis.LimitAccessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author zxh
 */
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    /**
     * 其他的错误
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> defaultExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e){

        // 业务异常，不打印堆栈信息
        if(e instanceof AppBusinessException){
            log.debug("AppBusinessException错误信息：",e);
        } else if(e instanceof InventoryBusinessException){
            log.debug("InventoryBusinessException错误信息：",e);
        } else if(e instanceof BusinessException){
            log.debug("BusinessException错误信息：",e);
        } else if(e instanceof FundBusinessException){
            log.debug("FundBusinessException错误信息：",e);
        } else{
            log.error("错误信息：",e);
        }

        if(e instanceof ApiBusinessException){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResultUtil.NG(
                            ((ApiBusinessException) e).getEnumData(),
                            ((ApiBusinessException) e).getMessage(),
                            request)
            );
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResultUtil.NG(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ResultEnum.SYSTEM_ERROR,
                            e,
                            e.getMessage(),
                            request)
            );
        }
    }

    /**
     * 更新出错时，设置返回的head，body
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = UpdateErrorException.class)
    @ResponseBody
    public ResponseEntity<Object> updateErrorExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e){
        log.error("错误信息：",e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResultUtil.NG(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ResultEnum.SYSTEM_ERROR,
                        e,
                        e.getMessage(),
                        request)
        );
    }

    /**
     * 更新出错时，设置返回的head，body
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = JWTAuthException.class)
    @ResponseBody
    public ResponseEntity<Object> jwtAuthExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e){
        log.error("错误信息：",e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResultUtil.NG(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ResultEnum.SYSTEM_ERROR,
                        e,
                        e.getMessage(),
                        request)
        );
    }

    /**
     * 限流，设置返回的head，body
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = LimitAccessException.class)
    @ResponseBody
    public ResponseEntity<Object> LimitAccessExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e){
        log.error("错误信息：",e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResultUtil.NG(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ResultEnum.SYSTEM_ERROR,
                        e,
                        e.getMessage(),
                        request)
        );
    }



}

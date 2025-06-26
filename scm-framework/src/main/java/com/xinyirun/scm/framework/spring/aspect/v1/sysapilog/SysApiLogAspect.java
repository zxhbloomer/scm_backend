package com.xinyirun.scm.framework.spring.aspect.v1.sysapilog;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.api.bo.log.sys.ApiLogBo;
import com.xinyirun.scm.bean.entity.mongo.log.api.SLogApiMongoEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.ExceptionUtil;
import com.xinyirun.scm.common.utils.IPUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.service.log.sys.ISLogApiService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.api.LogApiProducer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author zxh
 */
@Aspect
@Component
@Slf4j
public class SysApiLogAspect {

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private ISLogApiService iSLogApiService;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Pointcut("@annotation(com.xinyirun.scm.common.annotations.SysLogApiAnnotion)")
    public void sysApiLogAspect(){}

    @Autowired
    private LogApiProducer producer;

    /**
     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("sysApiLogAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        BigDecimal time =  new BigDecimal(System.currentTimeMillis() - beginTime);
//        SLogApiEntity entity = new SLogApiEntity();
        SLogApiMongoEntity entity = new SLogApiMongoEntity();
        try {
            ApiLogBo apiLogBo = printLog(point, time.longValue());
            if (systemConfigProperies.isLogSaveDb()){
                String jsonStr = JSONObject.toJSONString(result);
                entity.setOperation(apiLogBo.getRemark());
                entity.setUrl(apiLogBo.getUrl());
                entity.setTime(apiLogBo.getExecTime());
                entity.setHttp_method(apiLogBo.getHttpMethod());
                entity.setClass_name(apiLogBo.getClassName());
                entity.setClass_method(apiLogBo.getClassMethod());
                entity.setIp(apiLogBo.getIp());
                entity.setParams(apiLogBo.getParams());
                entity.setC_time(apiLogBo.getCreateDate());
                entity.setResult(jsonStr);
                if (jsonStr.indexOf(SystemConstants.API_ERROR) != -1) {
                    entity.setType(SystemConstants.LOG_FLG.NG);
                    entity.setException(jsonStr);
                } else {
                    entity.setType(SystemConstants.LOG_FLG.OK);
                }
            }
        } catch (Exception e) {
            entity.setException(e.getMessage());
            entity.setType(SystemConstants.LOG_FLG.NG);
            log.error("环绕切面发生异常：",e);
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求中获取requestId
        String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
        entity.setRequest_id(requestId);
        // 添加租户code，tenant_code
        entity.setTenant_code(DataSourceHelper.getCurrentDataSourceName());

        // 推送mq
        producer.mqSendMq(entity);
        return result;
    }

    /**
     *  异常通知
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "sysApiLogAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Map<String, String[]> params = request.getParameterMap();
        SAppConfigEntity sAppConfigEntity = isAppConfigService.getDataByAppKey(params.get("app_key")[0]);

//        SLogApiEntity entity = new SLogApiEntity();
        SLogApiMongoEntity entity = new SLogApiMongoEntity();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogApiAnnotion apiLog = method.getAnnotation(SysLogApiAnnotion.class);
        if (systemConfigProperies.isLogSaveDb()){

            entity.setOperation(apiLog.value());
            entity.setUrl(request.getRequestURL().toString());
            entity.setTime(null);
            entity.setApp_code(sAppConfigEntity.getCode());
            entity.setHttp_method(request.getMethod());
            entity.setClass_name(joinPoint.getTarget().getClass().getName());
            entity.setClass_method(((MethodSignature) joinPoint.getSignature()).getName());
            entity.setIp(IPUtil.getIpAdd());
            // 如果为 true, 不保存参数
            if (!apiLog.noParam()) {
                entity.setParams(convertArgsToJsonString(joinPoint.getArgs()));
            }
            entity.setC_time(LocalDateTime.now());
            entity.setType(SystemConstants.LOG_FLG.NG);
            entity.setException(ExceptionUtil.getException(e));
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求中获取requestId
        String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
        entity.setRequest_id(requestId);
        // 添加租户code，tenant_code
        entity.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
        // 向mq推送消息
        producer.mqSendMq(entity);
    }

    /**
     * 打印日志
     * @param joinPoint
     * @param time
     */
    private ApiLogBo printLog(ProceedingJoinPoint joinPoint, Long time) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogApiAnnotion apiLog = method.getAnnotation(SysLogApiAnnotion.class);
        ApiLogBo apiLogBo = ApiLogBo.builder()
            .className(joinPoint.getTarget().getClass().getName())
            .httpMethod(request.getMethod())
            .classMethod(((MethodSignature) joinPoint.getSignature()).getName())
//            .params( convertArgsToJsonString(joinPoint.getArgs()))
            .execTime(time)
            .remark(apiLog.value())
            .createDate(LocalDateTime.now())
            .url(request.getRequestURL().toString())
            .ip(IPUtil.getIpAdd())
            .build();
        // 如果为 true, 不保存参数
        if (!apiLog.noParam()) {
            apiLogBo.setParams(convertArgsToJsonString(joinPoint.getArgs()));
        }
        if(systemConfigProperies.isSysLog()){
            log.debug("======================日志开始================================");
            log.debug("日志名称         : " + apiLogBo.getRemark());
            log.debug("URL             : " + apiLogBo.getUrl());
            log.debug("HTTP方法         : " + apiLogBo.getHttpMethod());
            log.debug("IP               : " + apiLogBo.getIp());
            log.debug("类名             : " + apiLogBo.getClassName());
            log.debug("类方法           : " + apiLogBo.getClassMethod());
            log.debug("执行时间         : " + new BigDecimal(apiLogBo.getExecTime()).divide(BigDecimal.valueOf(1000)).toString() + "秒");
            log.debug("执行日期         : " + apiLogBo.getCreateDate());
            log.debug("参数             : " + apiLogBo.getParams());
            log.debug("======================日志结束================================");
        }
        return apiLogBo;
    }

    /**
     * 转换成json
     * @param args
     * @return
     */
    /**
     * 转换成json
     * @param args
     * @return
     */
    private String convertArgsToJsonString(Object[] args){
        if (args == null) {
            return null;
        }
        for (Object o : args) {
//            if(o instanceof BaseVo){
            // 使用JSONWriter.Feature.LargeObject来处理大对象
            return JSON.toJSONString(o, JSONWriter.Feature.LargeObject);
//            }
        }
        return null;
    }
}
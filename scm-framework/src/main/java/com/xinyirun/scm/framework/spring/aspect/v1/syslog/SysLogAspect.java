package com.xinyirun.scm.framework.spring.aspect.v1.syslog;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.entity.mongo.log.sys.SLogSysMongoEntity;
import com.xinyirun.scm.bean.system.bo.log.sys.SysLogBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.ExceptionUtil;
import com.xinyirun.scm.common.utils.IPUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.sys.LogPcSystemProducer;
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

/**
 * @author zxh
 */
@Aspect
@Component
@Slf4j
public class SysLogAspect {

//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

//    @Autowired
//    private ISLogService iSLogService;

    @Autowired
    private LogPcSystemProducer logProducer;

    @Pointcut("@annotation(com.xinyirun.scm.common.annotations.SysLogAnnotion)")
    public void sysLogAspect(){}

    /**
     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("sysLogAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        BigDecimal time =  new BigDecimal(System.currentTimeMillis() - beginTime);
//        SLogSysEntity entity = new SLogSysEntity();
        SLogSysMongoEntity entity = new SLogSysMongoEntity();
        try {
            SysLogBo sysLogBo = printLog(point, time.longValue());
            if (systemConfigProperies.isLogSaveDb()){
                entity.setOperation(sysLogBo.getRemark());
                entity.setUrl(sysLogBo.getUrl());
                entity.setTime(sysLogBo.getExecTime());
                entity.setHttp_method(sysLogBo.getHttpMethod());
                entity.setClass_name(sysLogBo.getClassName());
                entity.setClass_method(sysLogBo.getClassMethod());
                entity.setIp(sysLogBo.getIp());
                entity.setParams(sysLogBo.getParams());
                entity.setC_time(sysLogBo.getCreateDate());
                entity.setType(SystemConstants.LOG_FLG.OK);
                // 获取session
                Object session = ServletUtil.getUserSession();
                String userSessionJson = null;
                if(session != null){
                    UserSessionBo userSession = (UserSessionBo)session;
                    userSessionJson = JSON.toJSONString(userSession, JSONWriter.Feature.LargeObject);
                    entity.setUser_name(userSession.getUser_info().getLogin_name());
                    entity.setStaff_name(userSession.getStaff_info().getName());
                }
                entity.setSession(userSessionJson);
                entity.setException(null);
                // entity.setResult(JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            entity.setException(e.getMessage());
            entity.setType(SystemConstants.LOG_FLG.NG);
            log.error("环绕切面发生异常：",e);
            log.error("环绕切面发生异常--point信息：" ,point);
        }
//        iSLogService.asyncSave(entity);
        // 推送webSocket
//        simpMessagingTemplate.convertAndSend(WebSocketConstants.WEBSOCKET_HEARTBEATING_PATH, JSONObject.toJSONString(entity));
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求中获取requestId
        String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
        entity.setRequest_id(requestId);
        // 设置租户code
        entity.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
        // 向mq推送消息
        logProducer.mqSendMq(entity);
        return result;
    }

    /**
     *  异常通知
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "sysLogAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

//        SLogSysEntity entity = new SLogSysEntity();
        SLogSysMongoEntity entity = new SLogSysMongoEntity();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogAnnotion sysLog = method.getAnnotation(SysLogAnnotion.class);
        if (systemConfigProperies.isLogSaveDb()){
            entity.setOperation(sysLog.value());
            entity.setUrl(request.getRequestURL().toString());
            entity.setTime(null);
            entity.setHttp_method(request.getMethod());
            entity.setClass_name(joinPoint.getTarget().getClass().getName());
            entity.setClass_method(((MethodSignature) joinPoint.getSignature()).getName());
            entity.setIp(IPUtil.getIpAdd());
            entity.setParams(convertArgsToJsonString(joinPoint.getArgs()));
            entity.setC_time(LocalDateTime.now());
            entity.setType(SystemConstants.LOG_FLG.NG);
            // 获取session
            Object session = ServletUtil.getUserSession();
            String userSessionJson = null;
            if(session != null){
                UserSessionBo userSession = (UserSessionBo)session;
                userSessionJson = JSON.toJSONString(userSession);
                entity.setUser_name(userSession.getUser_info().getLogin_name());
                entity.setStaff_name(userSession.getStaff_info().getName());
            }
            entity.setSession(userSessionJson);
            entity.setException(ExceptionUtil.getException(e));
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求中获取requestId
        String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
        entity.setRequest_id(requestId);
        // 设置租户code
        entity.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
        // 向mq推送消息
        logProducer.mqSendMq(entity);
    }

    /**
     * 打印日志
     * @param joinPoint
     * @param time
     */
    private SysLogBo printLog(ProceedingJoinPoint joinPoint, Long time) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogAnnotion sysLog = method.getAnnotation(SysLogAnnotion.class);
        SysLogBo sysLogBo = SysLogBo.builder()
            .className(joinPoint.getTarget().getClass().getName())
            .httpMethod(request.getMethod())
            .classMethod(((MethodSignature) joinPoint.getSignature()).getName())
            .params( convertArgsToJsonString(joinPoint.getArgs()))
            .execTime(time)
            .remark(sysLog.value())
            .createDate(LocalDateTime.now())
            .url(request.getRequestURL().toString())
            .ip(IPUtil.getIpAdd())
            .build();
        if(systemConfigProperies.isSysLog()){
            log.debug("======================日志开始================================");
            log.debug("日志名称         : " + sysLogBo.getRemark());
            log.debug("URL             : " + sysLogBo.getUrl());
            log.debug("HTTP方法         : " + sysLogBo.getHttpMethod());
            log.debug("IP               : " + sysLogBo.getIp());
            log.debug("类名             : " + sysLogBo.getClassName());
            log.debug("类方法           : " + sysLogBo.getClassMethod());
            log.debug("执行时间         : " + new BigDecimal(sysLogBo.getExecTime()).divide(BigDecimal.valueOf(1000)).toString() + "秒");
            log.debug("执行日期         : " + sysLogBo.getCreateDate());
            log.debug("参数             : " + sysLogBo.getParams());
            log.debug("======================日志结束================================");
        }
        return sysLogBo;
    }

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
            String rtn = null;
            try {
                // 使用JSONWriter.Feature.LargeObject来处理大对象
                return JSON.toJSONString(o, JSONWriter.Feature.LargeObject);
            } catch (Exception e) {
                rtn=null;
            }

            if(o==null){
                rtn=null;
            }
            return rtn;
//            }
        }
        return null;
    }
}
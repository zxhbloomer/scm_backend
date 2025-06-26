package com.xinyirun.scm.framework.spring.aspect.v1.datachangelog;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.bo.log.sys.SysLogBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.ExceptionUtil;
import com.xinyirun.scm.common.utils.IPUtil;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.datachange.LogDataChangeProducer;
import jakarta.servlet.http.HttpServletRequest;
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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zxh
 */
@Aspect
@Component
@Slf4j
public class DataChangeOperateLogAspect {

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Pointcut("@annotation(com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation)")
    public void dataChangeLogAspect(){}

    @Autowired
    private LogDataChangeProducer producer;

    @Autowired
    private ISConfigService isConfigService;

    /**
     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("dataChangeLogAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        BigDecimal time =  new BigDecimal(System.currentTimeMillis() - beginTime);
        SLogDataChangeOperateMongoEntity entity = new SLogDataChangeOperateMongoEntity();
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            DataChangeOperateAnnotation annotation = method.getAnnotation(DataChangeOperateAnnotation.class);

            SysLogBo sysLogBo = getLog(point, time.longValue());
            if (systemConfigProperies.isLogSaveDb()){
                entity.setPage_name(annotation.page_name());
                entity.setOperation(annotation.value());
                entity.setUrl(sysLogBo.getUrl());
                entity.setTime(sysLogBo.getExecTime());
                entity.setHttp_method(sysLogBo.getHttpMethod());
                entity.setClass_name(sysLogBo.getClassName());
                entity.setClass_method(sysLogBo.getClassMethod());
                entity.setIp(sysLogBo.getIp());
                entity.setType(SystemConstants.LOG_FLG.OK);
                // 获取session
                Object session = ServletUtil.getUserSession();
                String userSessionJson = null;
                if(session != null){
                    UserSessionBo userSession = (UserSessionBo)session;
                    userSessionJson = JSON.toJSONString(userSession);
                    entity.setUser_name(userSession.getUser_info().getLogin_name());
                    entity.setStaff_name(userSession.getStaff_info().getName());
                    entity.setStaff_id(userSession.getStaff_info().getId().toString());
                } else {
                    entity.setUser_name(SecurityUtil.getUser_name());
                    entity.setStaff_name(SecurityUtil.getUser_name());
                    entity.setStaff_id(SecurityUtil.getStaff_id().toString());
                }
                entity.setException(null);
            }
        } catch (Exception e) {
            entity.setException(e.getMessage());
            entity.setType(SystemConstants.LOG_FLG.NG);
            log.error("环绕切面发生异常：",e);
            log.error("环绕切面发生异常--point信息：" ,point);
        }
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求中获取requestId
        String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
        entity.setRequest_id(requestId);
        entity.setOperate_time(LocalDateTime.now());

        SConfigEntity config = isConfigService.selectByKey(SystemConstants.LOG_DATA_CHANGE);
        if (config != null && "1".equals(config.getValue())) {
            // 向mq推送消息
            producer.mqSendMq(entity);
        }

        return result;
    }

    /**
     *  异常通知
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "dataChangeLogAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        SLogDataChangeOperateMongoEntity entity = new SLogDataChangeOperateMongoEntity();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataChangeOperateAnnotation sysLog = method.getAnnotation(DataChangeOperateAnnotation.class);
        if (systemConfigProperies.isLogSaveDb()){
            entity.setPage_name(sysLog.page_name());
            entity.setOperation(sysLog.value());
            entity.setUrl(request.getRequestURL().toString());
            entity.setTime(null);
            entity.setHttp_method(request.getMethod());
            entity.setClass_name(joinPoint.getTarget().getClass().getName());
            entity.setClass_method(((MethodSignature) joinPoint.getSignature()).getName());
            entity.setIp(IPUtil.getIpAdd());
            entity.setType(SystemConstants.LOG_FLG.NG);
            // 获取session
            Object session = ServletUtil.getUserSession();
            String userSessionJson = null;
            if(session != null){
                UserSessionBo userSession = (UserSessionBo)session;
                userSessionJson = JSON.toJSONString(userSession);
                entity.setUser_name(userSession.getUser_info().getLogin_name());
                entity.setStaff_name(userSession.getStaff_info().getName());
                entity.setStaff_id(userSession.getStaff_info().getId().toString());
            }
            entity.setException(ExceptionUtil.getException(e));
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求中获取requestId
        String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
        entity.setRequest_id(requestId);
        entity.setOperate_time(LocalDateTime.now());

        SConfigEntity config = isConfigService.selectByKey(SystemConstants.LOG_DATA_CHANGE);
        if (config != null && "1".equals(config.getValue())) {
            // 向mq推送消息
            producer.mqSendMq(entity);
        }

    }

    /**
     * getLog
     */
    private SysLogBo getLog(ProceedingJoinPoint joinPoint, Long time) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataChangeOperateAnnotation sysLog = method.getAnnotation(DataChangeOperateAnnotation.class);
        SysLogBo sysLogBo = SysLogBo.builder()
                .className(joinPoint.getTarget().getClass().getName())
                .httpMethod(request.getMethod())
                .classMethod(((MethodSignature) joinPoint.getSignature()).getName())
                .execTime(time)
                .remark(sysLog.value())
                .createDate(LocalDateTime.now())
                .url(request.getRequestURL().toString())
                .ip(IPUtil.getIpAdd())
                .build();
        return sysLogBo;
    }

}
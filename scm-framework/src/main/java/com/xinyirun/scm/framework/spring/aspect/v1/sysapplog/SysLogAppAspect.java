package com.xinyirun.scm.framework.spring.aspect.v1.sysapplog;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.app.bo.log.sys.AppLogBo;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import com.xinyirun.scm.bean.entity.mongo.log.app.SLogAppMongoEntity;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.ExceptionUtil;
import com.xinyirun.scm.common.utils.IPUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.jwt.JwtUtil;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserService;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.app.LogAppProducer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SysLogAppAspect {

    @Value("${scm.security.jwt.base64-secret}")
    private String base64Secret;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

/*    @Autowired
    private AppISLogAppService iSLogService;*/

    @Autowired
    private AppIMUserService appIMUserService;

    @Pointcut("@annotation(com.xinyirun.scm.common.annotations.SysLogAppAnnotion)")
    public void sysLogAppAspect() {
    }

    @Autowired
    private LogAppProducer producer;

    /**
     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("sysLogAppAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        BigDecimal time = new BigDecimal(System.currentTimeMillis() - beginTime);
//        SLogAppEntity entity = new SLogAppEntity();
        SLogAppMongoEntity entity = new SLogAppMongoEntity();
        try {
            AppLogBo sysLogBo = printLog(point, time.longValue());
            if (systemConfigProperies.isLogSaveDb()) {
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
                entity.setResult(JSONObject.toJSONString(result));

                String jwtUserJson = null;
                log.debug("app日志==> Authentication: {}", SecurityUtil.getAuthentication());
                if (SecurityUtil.getAuthentication() != null) {
                    AppUserBo bo = appIMUserService.getUserBean(SecurityUtil.getStaff_id(), SystemConstants.LOGINUSER_OR_STAFF_ID.STAFF_ID);
                    jwtUserJson = JSON.toJSONString(bo, JSONWriter.Feature.LargeObject);
                    log.debug("app日志==> 用户名: {}", jwtUserJson);
                    entity.setUser_name(bo.getApp_user_info().getLogin_name());
                    entity.setStaff_name(bo.getApp_staff_info().getStaff_name());

                } else {
                    AppMUserJwtTokenVo appMUserJwtTokenVo;
                    if ("/scm/api/app/oauth/token".equals(entity.getUrl())) {
                        try {
                            // 尝试解析是否参数，如果解析成功说明时登录操作
                            appMUserJwtTokenVo = JSON.parseObject(entity.getParams(), AppMUserJwtTokenVo.class);
                            AppJwtBaseBo bo;
                            try {
                                String jwtJson = JwtUtil.getUserStringByToken(appMUserJwtTokenVo.getToken(), base64Secret);
                                bo = JSON.parseObject(jwtJson, AppJwtBaseBo.class);
                            } catch (Exception e) {
                                log.error("around error", e);
                                log.debug("转换jwt--->出错");
                                throw new JWTAuthException("token不正确，不能正确解析!");
                            }
                            entity.setUser_name(bo.getUsername());
                        } catch (Exception e) {

                        }
                    }


                }

                entity.setSession(jwtUserJson);
                entity.setException(null);
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
        // 向mq推送消息
        producer.mqSendMq(entity);
        return result;
    }

    /**
     * 异常通知
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "sysLogAppAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

//        SLogAppEntity entity = new SLogAppEntity();
        SLogAppMongoEntity entity = new SLogAppMongoEntity();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogAppAnnotion sysLog = method.getAnnotation(SysLogAppAnnotion.class);
        if (systemConfigProperies.isLogSaveDb()) {
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
            log.debug("app日志==> 用户名: {}", SecurityUtil.getStaff_id());
            if (SecurityUtil.getStaff_id() != null) {
                AppUserBo bo = appIMUserService.getUserBean(SecurityUtil.getStaff_id(), SystemConstants.LOGINUSER_OR_STAFF_ID.STAFF_ID);
                String jwtUserJson = JSON.toJSONString(bo);
                log.debug("app日志==> AppUserBo: {}", jwtUserJson);
                entity.setUser_name(bo.getApp_user_info().getLogin_name());
                entity.setStaff_name(bo.getApp_staff_info().getStaff_name());

                entity.setSession(jwtUserJson);
            }
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
     *
     * @param joinPoint
     * @param time
     */
    private AppLogBo printLog(ProceedingJoinPoint joinPoint, Long time) {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogAppAnnotion sysLogApp = method.getAnnotation(SysLogAppAnnotion.class);
        AppLogBo sysLogBo = AppLogBo.builder()
                .className(joinPoint.getTarget().getClass().getName())
                .httpMethod(request.getMethod())
                .classMethod(((MethodSignature) joinPoint.getSignature()).getName())
                .params(convertArgsToJsonString(joinPoint.getArgs()))
                .execTime(time)
                .remark(sysLogApp.value())
                .createDate(LocalDateTime.now())
                .url(request.getRequestURI().toString())
                .ip(IPUtil.getIpAdd())
                .build();
        if (systemConfigProperies.isSysLog()) {
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
     *
     * @param args
     * @return
     */
    private String convertArgsToJsonString(Object[] args) {
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
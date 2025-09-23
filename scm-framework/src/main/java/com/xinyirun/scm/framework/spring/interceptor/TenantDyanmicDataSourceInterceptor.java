package com.xinyirun.scm.framework.spring.interceptor;

import com.xinyirun.scm.bean.system.bo.tenant.manager.user.STenantManagerBo;
import com.xinyirun.scm.common.enums.datasource.TenantStatusEnum;
import com.xinyirun.scm.common.utils.DateUtils;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.datasource.properties.DataSourceProperties;
import com.xinyirun.scm.common.utils.logging.TenantLogContextHolder;
import com.xinyirun.scm.core.tenant.service.business.login.ISTenantManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.sql.DataSource;

/**
 * 租户上下文拦截器，用于在请求中提取租户ID并设置到日志MDC中
 *
 * @author
 */
@Component
@Slf4j
public class TenantDyanmicDataSourceInterceptor implements HandlerInterceptor {

    @Autowired
    private ISTenantManagerService masterTenantService;

    // 无需租户信息的公共路径集合
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/imagecode",
            "/scm/error"
    };

    /**
     * 在请求处理之前调用，设置租户ID到TenantContextHolder和MDC中
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        log.info("==================== 租户拦截器开始 ====================");
        log.info("请求URI: {}", requestURI);
        log.info("请求方法: {}", request.getMethod());
        log.info("当前线程: {}", Thread.currentThread().getName());
        
        // 检查是否是无需拦截的路径
        if (isPublicPath(requestURI)) {
            log.info("跳过公共路径，无需租户信息: {}", requestURI);
            return true;
        }

        // 从请求中获取租户ID
        String tenantId = extractTenantId(request);
        log.info("从请求头X-Tenant-ID获取到租户ID: {}", tenantId);

        if (StringUtils.isNotBlank(tenantId)) {
            log.info("开始设置租户上下文，租户ID: {}", tenantId);
            
            // 设置租户ID到MDC，用于日志文件按租户分离
            TenantLogContextHolder.setTenantId(tenantId);
            log.info("已设置租户日志上下文到MDC: {}", TenantLogContextHolder.getTenantId());
            
            // 检查是否已经存在数据源，如果不存在则注册新的数据源
            Boolean isNotExist = true;
            try {
                DataSource ds = DataSourceHelper.getDataSource(tenantId);
                isNotExist = false;
            } catch (Exception e) {
                log.error("没有对应的连接: {}", e.getMessage());
            }

            if (isNotExist) {
                //搜索默认数据库，去注册租户的数据源，下次进来直接session匹配数据源
                STenantManagerBo masterTenant = masterTenantService.searchByTenant(tenantId);
                if (masterTenant == null) {
                    throw new RuntimeException("无此租户:"+tenantId );
                }else if(TenantStatusEnum.DISABLE.getCode().equals(masterTenant.getStatus())){
                    throw new RuntimeException("租户["+tenantId+"]已停用" );
                }else if(masterTenant.getExpire_date()!=null){
                    if(masterTenant.getExpire_date().before(DateUtils.getNowDate())){
                        throw new RuntimeException("租户["+tenantId+"]已过期");
                    }
                }
                DataSourceProperties dataSourceProperties = new DataSourceProperties();
                dataSourceProperties.setUrl(masterTenant.getUrl());
                dataSourceProperties.setUsername(masterTenant.getUser_name());
                dataSourceProperties.setPassword(masterTenant.getPassword());
                dataSourceProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
                DataSourceHelper.addDataSource(dataSourceProperties);

                log.info("成功注册新租户数据源 - 租户: {}, URL: {}, 用户: {}", 
                        tenantId, masterTenant.getUrl(), masterTenant.getUser_name());
            } else {
                log.info("租户数据源已存在 - 当前租户: {}", tenantId);
            }
        } else {
            log.error("租户ID为空，拒绝请求 - URI: {}, 所有请求头: {}", requestURI, getAllHeaders(request));
            throw new RuntimeException("缺少租户信息");
        }
        
        // 设置动态数据源
        DataSourceHelper.use(tenantId);
        log.info("已设置动态数据源: {}", DataSourceHelper.getCurrentDataSourceName());
        log.info("==================== 租户拦截器完成 ====================");
        
        return true;
    }

    /**
     * 检查是否是公共路径，不需要租户信息
     *
     * @param requestURI 请求路径
     * @return 如果是公共路径返回true，否则返回false
     */
    private boolean isPublicPath(String requestURI) {
        if (requestURI == null) {
            return false;
        }
        for (String publicPath : PUBLIC_PATHS) {
            if (requestURI.indexOf(publicPath) >=0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在请求完成后调用，清除TenantContextHolder和MDC中的租户ID
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String currentTenant = TenantLogContextHolder.getTenantId();
        log.info("==================== 租户拦截器清理 ====================");
        log.info("清理前的租户ID: {}", currentTenant);
        log.info("请求URI: {}", request.getRequestURI());
        
        // 请求处理完成后，清除租户上下文
        TenantLogContextHolder.clear();
        DataSourceHelper.close();
        
        log.info("已清除租户上下文和数据源连接");
        log.info("==================== 租户拦截器清理完成 ====================");
    }

    /**
     * 从请求中提取租户ID
     * 根据实际业务情况修改这个方法的实现
     */
    private String extractTenantId(HttpServletRequest request) {
        // 1. 从请求头中获取
        String tenantId = request.getHeader("X-Tenant-ID");

        return tenantId;
    }
    
    /**
     * 获取所有请求头信息，用于调试
     */
    private String getAllHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            headers.append(name).append("=").append(request.getHeader(name)).append("; ");
        });
        return headers.toString();
    }
}

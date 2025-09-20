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
        log.info("&&&&&&&&&&&&&&&& 租户拦截 &&&&&&&&&&&&&&&&");
        String requestURI = request.getRequestURI();
        // 检查是否是无需拦截的路径
        if (isPublicPath(requestURI)) {
            log.debug("公共路径，无需租户信息: {}", requestURI);
            return true;
        }

        // 从请求中获取租户ID
        String tenantId = extractTenantId(request);


        if (StringUtils.isNotBlank(tenantId)) {
            // 设置租户ID到MDC，用于日志文件按租户分离
            TenantLogContextHolder.setTenantId(tenantId);
            
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

                log.info("&&&&&&&&&&& 已设置租户:{} 连接信息: {}", tenantId, masterTenant);
            }else{
                log.info("&&&&&&&&&&& 当前租户:{}", tenantId);
            }
        }else{
            throw new RuntimeException("缺少租户信息");
        }
        DataSourceHelper.use(tenantId);
        log.debug("设置动态数据连接：设置租户ID到MDC: {}", DataSourceHelper.getCurrentDataSourceName());
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
        // 请求处理完成后，清除租户上下文
        TenantLogContextHolder.clear();
        DataSourceHelper.close();
        log.debug("清除租户上下文");
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
}

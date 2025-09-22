package com.xinyirun.scm.ai.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 简化版会话工具类，仅保留AI模块需要的基础功能
 */
public class SessionUtils {

    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> organizationId = new ThreadLocal<>();
    private static final ThreadLocal<String> projectId = new ThreadLocal<>();

    public static String getUserId() {
        String id = userId.get();
        if (StringUtils.isNotEmpty(id)) {
            return id;
        }
        // 可以从请求头或其他方式获取用户ID
        return "system"; // 默认值
    }

    public static void setUserId(String id) {
        userId.set(id);
    }

    public static String getCurrentOrganizationId() {
        String orgId = organizationId.get();
        if (StringUtils.isNotEmpty(orgId)) {
            return orgId;
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String headerOrg = request.getHeader("ORGANIZATION");
            if (StringUtils.isNotEmpty(headerOrg)) {
                return headerOrg;
            }
        } catch (Exception e) {
            LogUtils.debug("获取组织ID失败: {}", e.getMessage());
        }
        return "default-org";
    }

    public static void setCurrentOrganizationId(String orgId) {
        organizationId.set(orgId);
    }

    public static String getCurrentProjectId() {
        String projId = projectId.get();
        if (StringUtils.isNotEmpty(projId)) {
            return projId;
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String headerProject = request.getHeader("PROJECT");
            if (StringUtils.isNotEmpty(headerProject)) {
                return headerProject;
            }
        } catch (Exception e) {
            LogUtils.debug("获取项目ID失败: {}", e.getMessage());
        }
        return "default-project";
    }

    public static void setCurrentProjectId(String projId) {
        projectId.set(projId);
    }

    public static String getHttpHeader(String headerName) {
        if (StringUtils.isBlank(headerName)) {
            return null;
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return request.getHeader(headerName);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearCurrentOrganizationId() {
        organizationId.remove();
    }

    public static void clearCurrentProjectId() {
        projectId.remove();
    }

    public static void clearUserId() {
        userId.remove();
    }
}
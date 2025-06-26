package com.xinyirun.scm.common.utils.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * permission 工具类
 * 
 */
public class PermissionUtils
{
    private static final Logger log = LoggerFactory.getLogger(PermissionUtils.class);

    /**
     * 查看数据的权限
     */
    public static final String VIEW_PERMISSION = "no.view.permission";

    /**
     * 创建数据的权限
     */
    public static final String CREATE_PERMISSION = "no.create.permission";

    /**
     * 修改数据的权限
     */
    public static final String UPDATE_PERMISSION = "no.update.permission";

    /**
     * 删除数据的权限
     */
    public static final String DELETE_PERMISSION = "no.delete.permission";

    /**
     * 导出数据的权限
     */
    public static final String EXPORT_PERMISSION = "no.export.permission";

    /**
     * 其他数据的权限
     */
    public static final String PERMISSION = "no.permission";

    /**
     * 权限错误消息提醒
     * 
     * @param permissionsStr 错误信息
     * @return 提示信息
     */
    public static String getMsg(String permissionsStr)
    {

        return "";
    }

    /**
     * 返回用户属性值
     *
     * @param property 属性名称
     * @return 用户属性值
     */
    public static Object getPrincipalProperty(String property)
    {
        return null;
    }
}

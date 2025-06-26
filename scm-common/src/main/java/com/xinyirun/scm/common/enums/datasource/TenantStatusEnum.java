package com.xinyirun.scm.common.enums.datasource;

/**
 * 租户状态
 * 
 * @author devjd
 */
public enum TenantStatusEnum
{
    NORMAL("1", "正常"), DISABLE("2", "停用");

    private final String code;
    private final String info;

    TenantStatusEnum(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
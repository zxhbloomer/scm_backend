package com.xinyirun.scm.common.enums;


public enum ResultEnum {
    IMPORT_DATA_ERROR(20001, "导入数据出错"),
    EXPORT_DATA_ERROR(20002, "导出数据出错"),
    UNKNOWN_ERROR(-9, "未知错误"),
    OK(0, "成功"),
    FAIL(-1, "失败"),
    CODE_ERROT(-1,"验证码验证失败"),
    SESSION_INVALID(-1,"session失效"),
    ACCESS_DENIED(-1,"您没有该权限！"),

    // 0-200 系统级
    SUCCESS(0, "操作成功"),
    //
    SYSTEM_ERROR(1, "系统错误"),
    // 自定义错误，可以修改label
    SYSTEM_CUSTOM_ERROR(2, "自定义错误"),
    //
    SYSTEM_DATA_ERROR(3, "数据异常"),
    //
    SYSTEM_DATA_NOT_FOUND(4, "数据不存在"),
    //
    SYSTEM_NOT_LOGIN(5, "请登录"),
    //
    SYSTEM_UPDATE_ERROR(6, "数据更新失败"),
    //
    SYSTEM_NO_ACCESS(7, "无权限访问"),
    //
    SYSTEM_REQUEST_PARAM_ERROR(8, "请求参数错误"),

    SYSTEM_BUSINESS_ERROR(9, "系统繁忙,请您稍后再试"),

    SYSTEM_INSERT_ERROR(10,"已存在"),

    SYSTEM_INSERT_FAIL(11,"数据添加失败"),

    SYSTEM_DELETE_FAIL(12,"数据删除失败"),
    SYSTEM_REPEAT_SUBMIT(13,"重复提交出错"),

    // 201-300，用户模块
    USER_LOGIN_ERROR(201, "登录名或登录密码不正确"),
    //
    USER_PASSWORD_LENGTH(202, "密码长度不符合要求"),
    //
    USER_USERNAME_SAME(203, "该用户名已存在"),
    //
    USER_PASSWORD_SAME(204, "密码不能与老密码重复"),
    //
    USER_NOT_FIND(205, "用户不存在"),
    //
    USER_HAS_DELETED(206, "该用户已经失效"),
    //
    USER_ACCOUNT_ERROR(207, "帐号异常，请联系客服"),
    //
    USER_OLD_PASSWORD_ERROR(208, "原密码错误"),
    //
    USER_AUTHENTICATION_ERROR(209, "用户认证出错"),
    USER_NO_PERMISSION_ERROR(210, "没有该权限"),
    USER_LOGIN_TIME_OUT_ERROR(211, "登录已失效"),
    USER_SESSION_TIME_OUT_ERROR(212, "session过期"),

    JWT_AUTHENTICATION_GET_METHOD_ERROR(300, "不支持GET方式的提交！"),
    JWT_AUTHENTICATION_HTTP_HEAD_INVALID(301, "提交的登录账号信息有误，不能正常解析！"),
    JWT_AUTHENTICATION_ERROR(302, "用户认证出错！"),
    JWT_AUTHENTICATION_NOT_ACTIVATED(303, "用户尚未激活！"),
    ;

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

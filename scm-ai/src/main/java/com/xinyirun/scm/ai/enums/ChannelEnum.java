/**
 * 消息渠道枚举，定义消息来源的不同渠道类型
 */
package com.xinyirun.scm.ai.enums;

public enum ChannelEnum {
    SYSTEM,
    // 
    WEB,
    WEB_PC, // pc端
    WEB_H5, // h5端
    WEB_VISITOR, // 访客端
    WEB_FLOAT, // 悬浮窗
    WEB_ADMIN, // 管理端
    // 
    IOS,
    ANDROID,
    // 
    ELECTRON,
    LINUX,
    MACOS,
    WINDOWS,
    // 
    FLUTTER,
    FLUTTER_WEB,
    FLUTTER_ANDROID,
    FLUTTER_IOS,
    FLUTTER_MACOS,
    FLUTTER_WINDOWS,
    FLUTTER_LINUX,
    // 
    UNIAPP,
    UNIAPP_WEB,
    UNIAPP_ANDROID,
    UNIAPP_IOS,
    // 
    WECHAT,
    WECHAT_MINI,
    WECHAT_MP,
    WECHAT_WORK,
    WECHAT_KEFU,
    WECHAT_CHANNEL,
    //
    EMAIL,           // 邮件
    SMS,             // 短信
    PHONE,           // 电话
    // 
    TEST,
    ;

    // 根据字符串查找对应的枚举常量
    public static ChannelEnum fromValue(String value) {
        for (ChannelEnum type : ChannelEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
    
    /**
     * 将客户端类型转换为中文显示
     * @param client 客户端类型字符串
     * @return 对应的中文名称
     */
    public static String toChineseDisplay(String client) {
        try {
            ChannelEnum clientEnum = fromValue(client);
            return clientEnum.toChineseDisplay();
        } catch (Exception e) {
            return client;
        }
    }
    
    /**
     * 获取当前枚举值的中文显示
     * @return 对应的中文名称
     */
    public String toChineseDisplay() {
        switch (this) {
            case SYSTEM:
                return "系统";
            case WEB:
                return "网页";
            case WEB_PC:
                return "网页PC端";
            case WEB_H5:
                return "网页H5端";
            case WEB_VISITOR:
                return "网页访客端";
            case WEB_ADMIN:
                return "网页管理端";
            case IOS:
                return "iOS";
            case ANDROID:
                return "安卓";
            case ELECTRON:
                return "桌面应用";
            case LINUX:
                return "Linux";
            case MACOS:
                return "macOS";
            case WINDOWS:
                return "Windows";
            case FLUTTER:
                return "Flutter";
            case FLUTTER_WEB:
                return "Flutter网页版";
            case FLUTTER_ANDROID:
                return "Flutter安卓版";
            case FLUTTER_IOS:
                return "Flutter iOS版";
            case FLUTTER_MACOS:
                return "Flutter macOS版";
            case FLUTTER_WINDOWS:
                return "Flutter Windows版";
            case FLUTTER_LINUX:
                return "Flutter Linux版";
            case UNIAPP:
                return "UniApp";
            case UNIAPP_WEB:
                return "UniApp网页版";
            case UNIAPP_ANDROID:
                return "UniApp安卓版";
            case UNIAPP_IOS:
                return "UniApp iOS版";
            case WECHAT:
                return "微信";
            case WECHAT_MINI:
                return "微信小程序";
            case WECHAT_MP:
                return "微信公众号";
            case WECHAT_WORK:
                return "企业微信";
            case WECHAT_KEFU:
                return "微信客服";
            case WECHAT_CHANNEL:
                return "微信渠道";
            case EMAIL:
                return "邮件";
            case SMS:
                return "短信";
            case PHONE:
                return "电话";
            case TEST:
                return "测试";
            default:
                return this.name();
        }
    }
}
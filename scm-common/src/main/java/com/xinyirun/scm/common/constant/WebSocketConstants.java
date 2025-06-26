package com.xinyirun.scm.common.constant;


/**
 * websocket常量池
 * @author wanshaojian
 * @since 2018-10-15
 */
public class WebSocketConstants {
    //webSocket相关配置
    //链接地址
    public static final String WEBSOCKET_PATH = "/notice";
    /**
     * 设置广播节点
     */
    //消息代理路径
    public static final String WEBSOCKET_BROADCAST_PATH = "/topic";
    // 指定用户发送（一对一）的前缀 /user/
    public static final String WEBSOCKET_2USER_PATH = "/user";

    /**
     * 订阅
     */
    //服务端生产地址,客户端订阅此地址以接收服务端生产的消息
    public static final String WEBSOCKET_HEARTBEATING_PATH = "/topic/beating";
    // 客户端向服务端发送消息需有/wms 前缀
    public static final String WEBSOCKET_2SERVER_PATH = "/topic";

    public static final String WEBSOCKET_SESSION = "WEBSOCKET_SESSION";

    /**
     * 订阅广播：测试
     */
    public static final String WEBSOCKET_BROADCAST_MESSAGE = "/topic/broadcast/message";
    public static final String WEBSOCKET_SENDTOUSER_MESSAGE = "/subscribe/message";

    public static final String WEBSOCKET_SENDTO_TEST = "/topic/user/message";
    public static final String WEBSOCKET_SENDTOUSER_TEST = "/user/message";

    // 同步错误日志发送：预警和错误日志链接
    public static final String WEBSOCKET_SYNC_LOG = "/topic/syncLog";

    // bpm审批待办：审批待办链接
    public static final String WEBSOCKET_BPM_APPROVE_NOTICE = "/topic/bpm/approve";
    public static final String WEBSOCKET_BPM_REFUSE_NOTICE = "/topic/bpm/refuse";
    public static final String WEBSOCKET_BPM_CANCEL_NOTICE = "/topic/bpm/cancel";
}

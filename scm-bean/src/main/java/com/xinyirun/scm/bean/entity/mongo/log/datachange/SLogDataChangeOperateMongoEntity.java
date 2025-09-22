//package com.xinyirun.scm.bean.entity.mongo.log.datachange;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.io.Serial;
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * <p>
// * 系统日志表
// * </p>
// *
// * @author zxh
// * @since 2019-07-13
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@Document(collection = "s_log_data_change_operate")
//public class SLogDataChangeOperateMongoEntity implements Serializable {
//
//
//    private static final long serialVersionUID = -3141158204567593818L;
//
//    @Id
//    private String id;
//
//    /**
//     * 异常"NG"，正常"OK"
//     */
//    private String type;
//
//    /**
//     * 操作用户
//     */
//    @Indexed
//    private String user_name;
//
//    /**
//     * 员工姓名
//     */
//    @Indexed
//    private String staff_name;
//    private String staff_id;
//
//    /**
//     * 操作说明
//     */
//    private String operation;
//
//    /**
//     * 耗时（毫秒）
//     */
//    private Long time;
//
//    private String class_name;
//
//    private String class_method;
//
//    /**
//     * HTTP方法
//     */
//    private String http_method;
//
//    /**
//     * url
//     */
//    private String url;
//
//    /**
//     * IP地址
//     */
//    private String ip;
//
//    /**
//     * 异常信息
//     */
//    private String exception;
//
//    /**
//     * 操作时间
//     */
//    private LocalDateTime operate_time;
//
//    /**
//     * 页面名称
//     */
//    private String page_name;
//
//    /**
//     * 请求id
//     */
//    @Indexed
//    private String request_id;
//
//    /**
//     * pc、app、api
//     */
//    @Indexed
//    private String terminal;
//
//
//    /**
//     * 返回信息
//     */
//    private String result;
//
//    /**
//     * 租户code
//     */
//    private String tenant_code;
//}

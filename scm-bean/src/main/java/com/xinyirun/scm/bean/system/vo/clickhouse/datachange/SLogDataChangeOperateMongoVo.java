//package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;
//
//import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.springframework.data.mongodb.core.index.Indexed;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//import java.util.List;
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
//public class SLogDataChangeOperateMongoVo implements Serializable {
//
//
//    private static final long serialVersionUID = 2509981451343639043L;
//
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
//    private String user_name;
//
//    /**
//     * 员工姓名
//     */
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
//     * 开始时间
//     */
//    private LocalDateTime start_time;
//
//    /**
//     * 结束时间
//     */
//    private LocalDateTime over_time;
//
//    List<SLogDataChangeMongoVo> dataChangeMongoVoList;
//
//    /**
//     * 换页条件
//     */
//    private PageCondition pageCondition;
//
//}

//package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;
//
//import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * <p>
// * api系统日志
// * </p>
// *
// * @author zxh
// * @since 2019-07-13
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//public class SLogDataChangeMainVo implements Serializable {
//
//    private static final long serialVersionUID = 2733203432100877997L;
//
//    private String id;
//
//    /**
//     * 业务类型
//     */
//    private String order_type;
//
//   /**
//    * 业务单号
//    */
//    private String order_code;
//
//
//    /**
//     * name
//     */
//    private String name;
//
//    /**
//     * 生成时间
//     */
//    private LocalDateTime c_time;
//
//    /**
//     * 最后更新时间：被动态更新
//     */
//    private LocalDateTime u_time;
//
//    /**
//     * 更新人名称
//     */
//    private String u_name;
//    private String u_id;
//
//    /**
//     * 请求id
//     */
//    private String request_id;
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
//}

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
// * api系统日志
// * </p>
// *
// * @author zxh
// * @since 2019-07-13
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@Document("s_log_data_change_main")
//public class SLogDataChangeMainMongoEntity implements Serializable {
//
//
//    private static final long serialVersionUID = -1038089104524103509L;
//
//    @Id
//    private String id;
//
//    /**
//     * 单号类型
//     */
//    private String order_type;
//
//   /**
//    * 单号
//    */
//   @Indexed
//    private String order_code;
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
//    @Indexed
//    private String request_id;
//
//    /**
//     * 租户code
//     */
//    private String tenant_code;
//}

//package com.xinyirun.scm.bean.entity.mongo.log.excelimport;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * @author Wang Qianfeng
// * @Description 导入日志, 存 mongodb
// * @date 2023/3/1 16:00
// */
//@Data
//@Document("s_log_import")
//public class SLogImportMongoEntity implements Serializable {
//
//    private static final long serialVersionUID = 6728694571366622709L;
//
//    @Id
//    private Long id;
//
//    /**
//     * 业务表id
//     */
//    private Integer serial_id;
//
//    /**
//     * 业务表类型
//     */
//    private String serial_type;
//
//    /**
//     * 异常"NG"，正常"OK"
//     */
//    private String type;
//
//    /**
//     * 页面code
//     */
//    private String page_code;
//
//    /**
//     * 页面名称
//     */
//    private String page_name;
//
//    /**
//     * 上传文件url
//     */
//    private String upload_url;
//
//    /**
//     * 错误信息url
//     */
//    private String error_url;
//
//    /**
//     * 数据导入-json
//     */
//    private String import_json;
//
//    /**
//     * 创建时间
//     */
//    private LocalDateTime c_time;
//
//
//    /**
//     * 创建人id
//     */
//    private Long c_id;
//
//    /**
//     * 租户code
//     */
//    private String tenant_code;
//}

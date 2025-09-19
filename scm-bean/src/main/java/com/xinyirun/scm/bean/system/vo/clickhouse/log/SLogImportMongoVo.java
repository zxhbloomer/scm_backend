package com.xinyirun.scm.bean.system.vo.clickhouse.log;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 导入数据日志
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogImportMongoVo implements Serializable {

    private static final long serialVersionUID = -245103355109322413L;

    private String id;

    /**
     * 业务表id
     */
    private Integer serial_id;

    /**
     * 业务表类型
     */
    private String serial_type;

    /**
     * 异常"NG"，正常"OK"
     */
    private String type;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * 页面名称
     */
    private String page_name;

    /**
     * 上传文件url
     */
    private String upload_url;

    /**
     * 错误信息url
     */
    private String error_url;

    /**
     * 数据导入-json
     */
    private String import_json;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}

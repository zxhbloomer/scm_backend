package com.xinyirun.scm.bean.system.vo.clickhouse.file;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 附件详情
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SFileMonitorInfoMongoVo implements Serializable {

    private static final long serialVersionUID = 5936517213863170420L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 附件id
     */
    private Integer f_id;

    /**
     * url
     */
    private String url;

    /**
     * 内网_url
     */
    private String internal_url;

    /**
     * 上传时间
     */
    private LocalDateTime timestamp;

    /**
     * 数据库字段的附件名称
     */
    private String file_name;

    /**
     * 前端传来的附件名称
     */
    private String fileName;

    /**
     * 附件大小
     */
    private BigDecimal file_size;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人手机号
     */
    private String u_phone;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 修改人姓名
     */
    private String u_name;


}

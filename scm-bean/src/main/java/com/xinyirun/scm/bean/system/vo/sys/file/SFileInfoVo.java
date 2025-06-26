package com.xinyirun.scm.bean.system.vo.sys.file;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
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
// @ApiModel(value = "附件详情", description = "附件详情")
public class SFileInfoVo implements Serializable {

    private static final long serialVersionUID = 8910240912288659278L;

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
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人手机号
     */
    private String c_phone;

    /**
     * 修改人手机号
     */
    private String u_phone;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
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

package com.xinyirun.scm.bean.entity.sys.file;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
@TableName("s_file_info")
public class SFileInfoEntity implements Serializable {

    private static final long serialVersionUID = 1457421476050324121L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 附件id
     */
    @TableField("f_id")
    private Integer f_id;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 备份时间
     */
    @TableField(value="backup_time")
    private LocalDateTime backup_time;

    /**
     * url
     */
    @TableField("url")
    private String url;

    /**
     * 内网_url
     */
    @TableField("internal_url")
    private String internal_url;

    /**
     * 上传时间
     */
    @TableField("timestamp")
    private LocalDateTime timestamp;

    /**
     * 附件名称
     */
    @TableField("file_name")
    private String file_name;

    /**
     * 附件大小
     */
    @TableField("file_size")
    private BigDecimal file_size;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人ID
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 备份状态
     */
    @TableField("status")
    private Boolean status;

    /**
     * 备份备注
     */
    @TableField("remark")
    private String remark;


}

package com.xinyirun.scm.bean.entity.business.releaseorder;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 放货指令/借货指令附件表
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_release_files")
public class BReleaseFilesEntity implements Serializable {

    
    private static final long serialVersionUID = 7827206073641347690L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * 放货指令id
     */
    @TableField("release_order_id")
    private String release_order_id;

    /**
     * 放货指令编号
     */
    @TableField("release_order_code")
    private String release_order_code;

    /**
     * 附件名称
     */
    @TableField("file_name")
    private String file_name;

    /**
     * 附件链接
     */
    @TableField("url")
    private String url;

    /**
     * 上传时间
     */
    @TableField("timestamp")
    private LocalDateTime timestamp;


}

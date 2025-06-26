package com.xinyirun.scm.bean.entity.log.operate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 操作日志明细表
 * </p>
 *
 * @author zxh
 * @since 2019-12-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_log_oper_detail")
public class SLogOperDetailEntity implements Serializable {

    private static final long serialVersionUID = -1178181697383332189L;
    
    /**
     * 操作日志记录
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作日志主表id
     */
    @TableField("oper_id")
    private Long oper_id;

    /**
     * 操作业务名
     */
    @TableField("name")
    private String name;

    /**
     * 业务类型（其它、新增、修改、逻辑删除、物理删除）
     */
    @TableField("type")
    private String type;

    /**
     * 操作说明
     */
    @TableField("oper_info")
    private String oper_info;

    /**
     * 表名
     */
    @TableField("table_name")
    private String table_name;

    /**
     * 列英文名称
     */
    @TableField("clm_name")
    private String clm_name;

    /**
     * 列中文名称
     */
    @TableField("clm_comment")
    private String clm_comment;

    /**
     * 旧值
     */
    @TableField("old_val")
    private String old_val;

    /**
     * 新值
     */
    @TableField("new_val")
    private String new_val;
}

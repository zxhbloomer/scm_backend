package com.xinyirun.scm.bean.entity.busniess.wms.warehouse.relation;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_warehouse_relation")
public class MWarehouseRelationEntity implements Serializable {

    private static final long serialVersionUID = -7034003323713530532L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号，00010001..
     */
    @TableField("code")
    private String code;

    /**
     * 儿子个数
     */
    @TableField("son_count")
    private Integer son_count;

    /**
     * 上级组织，null为根节点
     */
    @TableField("parent_id")
    private Long parent_id;

    /**
     * 根结点
     */
    @TableField("sys_id")
    private Long sys_id;

    /**
     * 关联单号
     */
    @TableField("serial_id")
    private Long serial_id;

    /**
     * 关联单号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 类型：10（一级）、20（二级）、30（三级）
     */
    @TableField("type")
    private String type;

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



}

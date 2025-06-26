package com.xinyirun.scm.bean.entity.busniess.warehouse.relation;

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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_warehouse_relation")
public class BWarehouseRelationEntity implements Serializable {

    private static final long serialVersionUID = -5876657602015682583L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联表id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 关联表名
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 仓库组权限CODE
     */
    @TableField("warehouse_relation_code_first")
    private String warehouse_relation_code_first;

    /**
     * 仓库组权限CODE
     */
    @TableField("warehouse_relation_code")
    private String warehouse_relation_code;

    /**
     * 员工id
     */
    @TableField("staff_id")
    private Integer staff_id;

    /**
     * 岗位id
     */
    @TableField("position_id")
    private Integer position_id;

}

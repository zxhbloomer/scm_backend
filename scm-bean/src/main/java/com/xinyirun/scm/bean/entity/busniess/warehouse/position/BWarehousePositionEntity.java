package com.xinyirun.scm.bean.entity.busniess.warehouse.position;

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
@TableName("b_warehouse_position")
public class BWarehousePositionEntity implements Serializable {

    private static final long serialVersionUID = 630771195614752959L;
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
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 仓库CODE
     */
    @TableField("warehouse_code")
    private String warehouse_code;

}

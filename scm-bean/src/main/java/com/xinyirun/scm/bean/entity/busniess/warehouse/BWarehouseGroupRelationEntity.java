package com.xinyirun.scm.bean.entity.busniess.warehouse;

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
 * 仓库关系表-一级
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_warehouse_group_relation")
public class BWarehouseGroupRelationEntity implements Serializable {

    private static final long serialVersionUID = 4551041892459521807L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 仓库组分类id
     */
    @TableField("warehouse_group_id")
    private Integer warehouse_group_id;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;


}

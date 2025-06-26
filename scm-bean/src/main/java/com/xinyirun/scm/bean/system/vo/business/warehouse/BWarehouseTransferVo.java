package com.xinyirun.scm.bean.system.vo.business.warehouse;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: BWarehouseGroupOneTransferVo
 * @Description: 仓库组1bean，为穿梭框服务
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "仓库组1bean，为穿梭框服务", description = "仓库组1bean，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class BWarehouseTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7085867209188297426L;

    /** 穿梭框 key */
    private Integer key;
    /** 穿梭框 label */
    private String label;
    /**
     * 租户id
     */
//    private Long tenant_id;

    /** 仓库组ID */
    private Integer warehouse_group_id;

    /** 穿梭框已经选择的仓库id */
    Integer [] group_warehouses;

}

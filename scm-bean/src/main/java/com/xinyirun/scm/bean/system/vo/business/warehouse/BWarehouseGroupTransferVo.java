package com.xinyirun.scm.bean.system.vo.business.warehouse;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: BWarehouseGroupTransferVo
 * @Description: 仓库组-仓库vo
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "仓库组-仓库vo，为穿梭框服务", description = "仓库组-仓库vo，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class BWarehouseGroupTransferVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = 7324807374698295890L;

    /**
     * 穿梭框：全部仓库
     */
    List<BWarehouseTransferVo> warehouse_all;

    /**
     * 穿梭框：该仓库组下，全部仓库
     */
    Integer [] group_warehouse;

    /**
     * 该仓库中下，仓库数量
     */
    int group_warehouse_count;
}

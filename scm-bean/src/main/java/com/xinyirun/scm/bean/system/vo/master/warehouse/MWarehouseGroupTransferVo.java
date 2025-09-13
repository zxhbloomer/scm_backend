package com.xinyirun.scm.bean.system.vo.master.warehouse;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: MWarehouseGroupTransferVo
 * @Description: 仓库组-仓库vo
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "仓库组-仓库vo，为穿梭框服务", description = "仓库组-仓库vo，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class MWarehouseGroupTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7574963911043937546L;

    /**
     * 穿梭框：全部仓库组
     */
    List<MWGroupTransferVo> warehouse_group_all;

    /**
     * 穿梭框：该仓库下，全部仓库组
     */
    Long [] warehouse_groups;

    /**
     * 该仓库下，仓库组数量
     */
    int warehouse_group_count;
}

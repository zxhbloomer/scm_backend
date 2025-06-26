package com.xinyirun.scm.bean.system.vo.master.warhouse;

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
public class MWarehouseStaffTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7574963911043937546L;

    /**
     * 穿梭框：全部仓库组
     */
    List<MWStaffTransferVo> warehouse_all;

    /**
     * 穿梭框：该仓库下，全部仓库组
     */
    Long [] warehouses;

    /**
     * 该员工下，仓库数量
     */
    int warehouse_count;
}

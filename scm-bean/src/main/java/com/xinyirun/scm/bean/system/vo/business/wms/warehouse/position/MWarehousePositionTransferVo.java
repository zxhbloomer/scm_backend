package com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: MWarehouseTransferVo
 * @Description: 仓库穿梭框使用bean
 * @Author: zxh
 * @date: 2020/1/10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MWarehousePositionTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6873609446858998678L;
    /**
     * 穿梭框：全部仓库
     */
    List<MWarehouseTransferVo> warehouse_all;

    /**
     * 穿梭框：该仓库组下，全部仓库
     */
    Long [] warehouses;

    /**
     * 该仓库组下，仓库数量
     */
    int warehouse_position_count;
}

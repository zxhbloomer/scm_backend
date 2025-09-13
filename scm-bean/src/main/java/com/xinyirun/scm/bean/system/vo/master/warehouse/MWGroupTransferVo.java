package com.xinyirun.scm.bean.system.vo.master.warehouse;

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
@EqualsAndHashCode(callSuper=false)
public class MWGroupTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 4577593238729914307L;

    /** 穿梭框 key */
    private Integer key;
    /** 穿梭框 label */
    private String label;

    /** 仓库ID */
    private Integer warehouse_id;

    /** 穿梭框已经选择的仓库组id */
    Integer [] warehouse_groups;

}

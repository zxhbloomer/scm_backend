package com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MWarehouseTransferVo
 * @Description: 仓库bean，为穿梭框服务
 * @Author: zxh
 * @date: 2020/1/10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MWarehouseTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8770973877366576732L;
    private String code;

    /** 穿梭框 key */
    private Long key;

    /** 穿梭框 label */
    private String label;

    private Boolean enable;

    /** 岗位ID */
    private Integer position_id;

    /** 穿梭框已经选择的仓储id */
    Integer [] warehouses;
}

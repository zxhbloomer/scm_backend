package com.xinyirun.scm.bean.system.vo.master.warhouse;

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
public class MWStaffTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7988503094057050620L;

    /** 穿梭框 key */
    private Integer key;
    private Integer id;
    /** 穿梭框 label */
    private String label;
    private String short_name;
    private String code;

    /** 员工ID */
    private Integer staff_id;

    /** 穿梭框已经选择的仓库id */
    Integer [] warehouse_ids;

}

package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: MStaffPositionTransferVo
 * @Description: 员工岗位vo
 * @Author: zxh
 * @date: 2020/1/10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工bean，为穿梭框服务", description = "员工bean，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class MStaffPositionTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8892463566681169124L;

    /**
     * 穿梭框：全部员工
     */
    List<MStaffTransferVo> staff_all;

    /**
     * 穿梭框：该岗位下，全部员工
     */
    Long [] staff_positions;

    /**
     * 该岗位下，员工数量
     */
    int staff_positions_count;
}

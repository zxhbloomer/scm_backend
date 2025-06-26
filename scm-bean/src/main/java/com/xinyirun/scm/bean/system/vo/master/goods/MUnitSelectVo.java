package com.xinyirun.scm.bean.system.vo.master.goods;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单位的下拉选项
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MUnitSelectVo {

    /**
     * 单位的下拉选项
     */
    List<MUnitVo> unit_datas;

    /**
     * 对应默认选中的值
     */
    MUnitVo active_data;
}

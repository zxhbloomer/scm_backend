package com.xinyirun.scm.bean.system.vo.business.warehouse.relation;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MOrgCountsVo
 * @Description: 记录仓库组下的数量
 * @Author: zxh
 * @date: 2019/12/17
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MRelationCountsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -5959786865695581048L;

    /** 仓库数据 数量 */
    private Long warehouse_count;
}

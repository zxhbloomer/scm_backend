package com.xinyirun.scm.bean.system.vo.business.warehouse.position;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BWarehousePositionVo implements Serializable {

    private static final long serialVersionUID = 2883771251644645761L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 关联表id
     */
    private Integer serial_id;

    /**
     * 关联表名
     */
    private String serial_type;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库CODE
     */
    private String warehouse_code;

}

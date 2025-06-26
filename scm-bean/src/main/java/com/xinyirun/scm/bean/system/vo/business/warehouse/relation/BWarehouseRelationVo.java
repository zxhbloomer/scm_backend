package com.xinyirun.scm.bean.system.vo.business.warehouse.relation;

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
public class BWarehouseRelationVo implements Serializable {

    private static final long serialVersionUID = 2246004249045695063L;
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
     * 仓库组权限CODE
     */
    private String warehouse_relation_code_first;

    /**
     * 仓库组权限CODE
     */
    private String warehouse_relation_code;

    /**
     * 员工ID
     */
    private Integer staff_id;

    /**
     * 岗位ID
     */
    private Integer position_id;

}

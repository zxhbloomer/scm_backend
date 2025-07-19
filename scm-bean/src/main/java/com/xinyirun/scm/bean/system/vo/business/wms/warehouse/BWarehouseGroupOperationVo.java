package com.xinyirun.scm.bean.system.vo.business.wms.warehouse;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 仓库组-仓库关系，权限名称
 * </p>
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BWarehouseGroupOperationVo implements Serializable {

    private static final long serialVersionUID = 4313951323128613813L;

    private Integer id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库组名称
     */
    private String warehouse_group_name;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

}

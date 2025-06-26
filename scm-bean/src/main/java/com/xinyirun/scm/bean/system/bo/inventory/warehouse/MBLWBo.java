package com.xinyirun.scm.bean.system.bo.inventory.warehouse;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 关于仓库三兄弟的大bean
 * 只考虑单对单
 * </p>
 *
 */
@Data
public class MBLWBo implements Serializable {

    private static final long serialVersionUID = -1740413232689518710L;

    /**
     * 仓库 ID
     */
    private Integer warehouse_id;

    /**
     * 仓库 编码
     */
    private String warehouse_code;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区 code
     */
    private String location_code;

    /**
     * 货位 ID
     */
    private Integer bin_id;

    /**
     * 货位 code
     */
    private String bin_code;
}


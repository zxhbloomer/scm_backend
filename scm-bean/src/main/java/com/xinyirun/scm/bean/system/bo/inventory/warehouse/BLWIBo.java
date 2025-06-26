package com.xinyirun.scm.bean.system.bo.inventory.warehouse;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 关于仓库三兄弟的大bean
 * 如果从仓库向下推，1对多关系，需要从warehousebean -> locationbean -> binbean 来推
 *
 * 外加库存bean，考虑混放情况，所以是个list
 * </p>
 *
 */
@Data
public class BLWIBo implements Serializable {

    private static final long serialVersionUID = -3197479099995706027L;

    private MBinBo bin;

    private MLocationBo location;

    private MWareHouseBo warehouse;

    private List<MInventoryBo> inventories;
}


package com.xinyirun.scm.bean.app.bo.inventory.out;

import com.xinyirun.scm.bean.app.bo.inventory.company.AppConsignorBo;
import com.xinyirun.scm.bean.app.bo.inventory.company.AppOwnerBo;
import com.xinyirun.scm.bean.app.bo.inventory.warehouse.AppBLWBo;
import com.xinyirun.scm.bean.app.bo.material.AppSkuBo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.common.enums.InventoryBusinessTypeEnum;
import com.xinyirun.scm.common.enums.InventoryTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库使用的bean
 */
@Data
@Builder
public class AppStockOutBo {

    // 库位
//    @NotNull(message = "库位id不能为空")
    private Integer bin_id;
//    @NotNull(message = "skuid不能为空")
    private Integer sku_id;
    // 入库数量
//    @NotNull(message = "入库数量不能为空")
    private BigDecimal count;
    // 是否锁定
//    @NotNull(message = "是否锁定不能为空")
    private Boolean lock;
    // 关联单据类型
//    @NotNull(message = "关联单据类型不能为空")
    private String serial_type;
    // 关联单据id
//    @NotNull(message = "关联单据不能为空")
    private Integer serial_id;
    // 委托方id
//    @NotNull(message = "委托方不能为空")
    private Integer consignor_id;
    // 货主id
//    @NotNull(message = "货主不能为空")
    private Integer owner_id;

    // 流水类型
    private InventoryTypeEnum inventoryTypeEnum;
    // 业务类型
    private InventoryBusinessTypeEnum inventoryBusinessTypeEnum;

    /**
     * 下面都是自动填充
     */
    // 库区
    private Integer location_id;
    // 仓库
    private Integer wareHouse_id;
    // 批次号
    private String lot;

    // sku bean
    private AppSkuBo sku;
    // 仓库三兄弟大bean
    private AppBLWBo blw;
    // 委托方Bean
    private AppConsignorBo consignor;
    // 货主Bean
    private AppOwnerBo owner;
    // 库存bean
    private List<MInventoryEntity> inventories;
    // 出库单实体类，别忘记加悲观锁
    private BOutEntity bOutEntity;

    /**
     * 当前符合条件的库存
     */
    private BigDecimal totalInventoryCount;

    /**
     * 当前符合条件的库存
     */
    private BigDecimal totalInventoryLockCount;

    // 入库数量：参与计算使用
    private BigDecimal calculate_count;
}

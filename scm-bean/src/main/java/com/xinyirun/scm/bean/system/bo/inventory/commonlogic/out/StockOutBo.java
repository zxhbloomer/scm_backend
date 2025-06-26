package com.xinyirun.scm.bean.system.bo.inventory.commonlogic.out;

import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.bo.inventory.company.ConsignorBo;
import com.xinyirun.scm.bean.system.bo.inventory.company.OwnerBo;
import com.xinyirun.scm.bean.system.bo.inventory.material.SkuBo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.BLWBo;
import com.xinyirun.scm.common.enums.InventoryBusinessTypeEnum;
import com.xinyirun.scm.common.enums.InventoryTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库使用的bean
 */
@Data
@Builder
public class StockOutBo implements Serializable {

    @Serial
    private static final long serialVersionUID = -625974688938939358L;
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
    private SkuBo sku;
    // 仓库三兄弟大bean
    private BLWBo blw;
    // 委托方Bean
    private ConsignorBo consignor;
    // 货主Bean
    private OwnerBo owner;
    // 库存bean
    private List<MInventoryEntity> inventories;
    // 出库单实体类，别忘记加悲观锁
    private BOutEntity bOutEntity;

    /**
     * 时间
     */
    private LocalDateTime dt;

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

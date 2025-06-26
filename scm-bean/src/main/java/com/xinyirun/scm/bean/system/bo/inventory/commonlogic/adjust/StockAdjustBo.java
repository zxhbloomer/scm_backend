package com.xinyirun.scm.bean.system.bo.inventory.commonlogic.adjust;

import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustDetailEntity;
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
 * 库存调整使用的 bean
 * 只能调整可用库存，锁定库存不能调整
 */
@Data
@Builder
public class StockAdjustBo implements Serializable {
    @Serial
    private static final long serialVersionUID = 8997193130007070735L;
    // 库位
    private Integer bin_id;
    private Integer sku_id;
    // 入库数量：不考虑增量，只考虑更新
    private BigDecimal count;
    private BigDecimal count_diff;
    // 关联单据类型
    private String serial_type;
    // 关联单据id
    private Integer serial_id;
    // 委托方id
    private Integer consignor_id;
    // 货主id
    private Integer owner_id;

    /**
     * 时间
     */
    private LocalDateTime dt;

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
    // 调整规则
    private String rule;
    // 单价
    private BigDecimal price;
    // 货值
    private BigDecimal amount;
    // 委托方Bean
    private ConsignorBo consignor;
    // 货主Bean
    private OwnerBo owner;
    // 库存bean
    private List<MInventoryEntity> inventories;
    // 调整单明细单实体类，别忘记加悲观锁
    private BAdjustDetailEntity bAdjustDetailEntity;
}

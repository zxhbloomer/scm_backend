package com.xinyirun.scm.bean.system.vo.master.inventory.query;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 货主库存查询vo：按货主和商品分组汇总
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MInventoryOwnerGoodsQueryVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 2791758696383975506L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 业务板块
     */
    private String business_name;
    /**
     * 行业
     */
    private String industry_name;
    /**
     * 分类
     */
    private String category_name;

    /**
     * 物料名称
     */
    private String sku_name;

    /**
     * 货主名
     */
    private String owner_name;
    private String owner_short_name;
    private Integer owner_id;

    /**
     * 库存数量
     */
    private BigDecimal qty_avaible;

    /**
     * 库存数量
     */
    private BigDecimal qty;

    /**
     * 锁定库存数量
     */
    private BigDecimal qty_lock;

    /**
     * 单位
     */
    private String unit_name;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}

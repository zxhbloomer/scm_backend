package com.xinyirun.scm.bean.system.vo.master.inventory.query;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存明细查询vo
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存", description = "库存")
public class MInventoryDetailQueryVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7446991695502837948L;

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
     * 编码
     */
    private String inventory_code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;
    private Integer[] warehouse_ids;

    /**
     * 仓库名
     */
    private String warehouse_name;
    private String warehouse_short_name;
    private String warehouse_type_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 库区名
     */
    private String location_name;
    private String location_short_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库区名
     */
    private String bin_name;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 规格
     */
    private String spec;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料名称
     */
    private String sku_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 货主名
     */
    private String owner_name;
    private String owner_short_name;

    /**
     * 批次
     */
    private String lot;

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
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 货主ID
     */
    private Integer[] owner_ids;

    /**
     * 仓库类型
     */
    private String[] warehouse_types;

    /**
     * 商品属性
     */
    private String goods_prop;

    /**
     * 是否值 中林 环境
     */
    private Boolean isZLEnvironment;

//    private Integer order_id;


//    private String order_type;

    /**
     * 最后一次入库时间
     */
    private LocalDateTime in_time;

    /**
     * 最后一次出库时间
     */
    private LocalDateTime out_time;

    /**
     * 最后一次调整时间
     */
    private LocalDateTime up_time;

}

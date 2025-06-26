package com.xinyirun.scm.bean.system.vo.master.inventory;

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
 * 库存
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MInventoryVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7446991695502837948L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;
    private Integer[] warehouse_ids;

    /**
     * 仓库名
     */
    private String warehouse_name;

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

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库区名
     */
    private String bin_name;

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
     * 物料名称
     */
    private String goods_name;

    /**
     * 物料编码
     */
    private String goods_code;

    /**
     * 品名/物料名称/规格/物料编码
     */
    private String key_word;

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
    private String unit;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 货主ID
     */
    private Integer[] owner_ids;

}

package com.xinyirun.scm.bean.system.vo.business.materialconvert;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialConvertVo implements Serializable {

    private static final long serialVersionUID = 8899421862749492171L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 序号
     */
    private Integer idx;

    /**
     * 转换前物料条数
     */
    private Integer count;

    /**
     * 物料转换id
     */
    private Integer material_convert_id;

    /**
     * 单号
     */
    private String code;

    /**
     * 名称
     */
    private String name;
    private String convert_name;

    /**
     * 状态
     */
    private String status;
    private String status_name;

    /**
     * 类型 0单次任务 1定时任务
     */
    private String type;
    private String type_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 仓库名称
     */
    private String warehouse_name;

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
    private String c_name;

    /**
     * 修改人id
     */
    private Long u_id;
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    List<BMaterialConvertDetailVo> detailList;

    /**
     * 单据状态
     */
    private Integer is_effective;

    /**
     * 原物料id
     */
    private Integer source_sku_id;

    /**
     * 原物料code
     */
    private String source_sku_code;

    /**
     * 原规格
     */
    private String source_spec;

    /**
     * 原物料名称
     */
    private String source_goods_name;

    /**
     * 新物料id
     */
    private Integer target_sku_id;

    /**
     * 新物料code
     */
    private String target_sku_code;

    /**
     * 物料id
     */
    private Boolean is_sku;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

    /**
     * 新规格
     */
    private String target_spec;

    /**
     * 新物料名称
     */
    private String target_goods_name;

    /**
     * 转换后比例
     */
    private BigDecimal calc;

    /**
     * 规格
     */
    private String spec;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 出库时间起
     */
    private LocalDateTime start_time;

    /**
     * 出库时间止
     */
    private LocalDateTime over_time;

    private Integer data_version;
}

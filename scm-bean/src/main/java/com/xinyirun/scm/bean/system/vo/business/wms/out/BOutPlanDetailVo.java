package com.xinyirun.scm.bean.system.vo.business.wms.out;


import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划详情
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划详情", description = "出库计划详情")
public class BOutPlanDetailVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -753516863478304988L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 子计划单号
     */
    private String code;

    /**
     * 订单明细编号
     */
    private String order_detail_no;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 外部系统单号
     */
    private String plan_extra_code;

    /**
     * 出库类型
     */
    private String type;

    /**
     * 出库类型值
     */
    private String type_name;

    /**
     * 计划主表id
     */
    private Integer plan_id;

    /**
     * 计划主表code
     */
    private String plan_code;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 物料名
     */
    private String goods_name;

    /**
     * 物料编码
     */
    private String spec_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料规格
     */
    private String spec;


    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 别名
     */
    private String alias;

    /**
     * 物料单价
     */
    private BigDecimal price;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 换算单位
     */
    private String unit_name;

    /**
     * 库存计量单位
     */
    private String unit;

    /**
     * 换算单位
     */
    private BigDecimal hs_gx;

    /**
     * 换算单位id
     */
    private Integer unit_convert_id;

    /**
     * 数量
     */
    private BigDecimal count;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名
     */
    private String warehouse_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区名
     */
    private String location_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位名
     */
    private String bin_name;

    /**
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户编码
     */
    private String client_code;

    /**
     * 客户名
     */
    private String client_name;

    /**
     * 客户id
     */
    private Integer customer_id;

    /**
     * 客户编码
     */
    private String customer_code;

    /**
     * 客户名
     */
    private String customer_name;

    /**
     * 委托方code
     */
    private String consignor_code;

    /**
     * 委托方名
     */
    private String consignor_name;

    /**
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 订单Id
     */
    private Integer order_id;

    /**
     * 订单类型
     */
    private String order_type;

    /**
     * 货主名
     */
    private String owner_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 待处理数量
     */
    private BigDecimal pending_count;

    /**
     * 待处理重量
     */
    private BigDecimal pending_weight;

    /**
     * 待处理体积
     */
    private BigDecimal pending_volume;

    /**
     * 已处理(出/入)库数量
     */
    private BigDecimal has_handle_count;

    /**
     * 已处理(出/入)库重量
     */
    private BigDecimal has_handle_weight;

    /**
     * 已处理(出/入)库体积
     */
    private BigDecimal has_handle_volume;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 审核人id
     */
    private Integer auditor_id;

    /**
     * 审核时间
     */
    private LocalDateTime audit_dt;

    /**
     * 审核意见
     */
    private String e_opinion;

    /**
     * 审核意见
     */
    private String audit_info;

    /**
     * 最大出库量
     */
    private BigDecimal max_count;

    /**
     * 作废备注
     */
    private String cancel_remark;

    /**
     * 上浮百分比
     */
    private BigDecimal over_inventory_upper;

    /**
     * 是否开启上浮比例
     */
    private Boolean over_inventory_policy;

    private BigDecimal actual_count;

    /**
     * 实际重量
     */
    private BigDecimal actual_weight;

    /**
     * 备注
     */
    private String remark;

    /**
     * 详情备注
     */
    private String detail_remark;


    /**
     * 退货数量
     */
    private BigDecimal return_qty;
}

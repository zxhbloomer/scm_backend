package com.xinyirun.scm.bean.system.vo.wms.inplan;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库计划明细
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BInPlanDetailVo extends PageCondition {


    @Serial
    private static final long serialVersionUID = 2551882898182644260L;


    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 入库计划id
     */
    private Integer in_plan_id;

    /**
     * 串号id
     */
    private Integer serial_id;

    /**
     * 串号编码
     */
    private String serial_code;

    /**
     * 串号类型
     */
    private String serial_type;

    /**
     * 项目编码
     */
    private String project_code;

    /**
     * 合同id
     */
    private Integer contract_id;

    /**
     * 合同编码
     */
    private String contract_code;

    /**
     * 订单数量
     */
    private BigDecimal order_qty;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 订单编码
     */
    private String order_code;

    /**
     * 订单明细id
     */
    private Integer order_detail_id;

    /**
     * 商品编码
     */
    private String goods_code;

    /**
     * 商品id
     */
    private Integer goods_id;

    private String goods_name;

    /**
     * sku id
     */
    private Integer sku_id;

    /**
     * sku编码
     */
    private String sku_code;
    private String sku_name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 订单价格
     */
    private BigDecimal order_price;

    /**
     * 数量
     */
    private BigDecimal qty;

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
    private String warehouse_name;

    /**
     * 库位id
     */
    private Integer location_id;
    private String location_name;

    /**
     * 库位id
     */
    private Integer bin_id;
    private String bin_name;

    /**
     * 供应商id
     */
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    private String supplier_code;
    private String supplier_name;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 处理中数量
     */
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    private BigDecimal processed_volume;

    /**
     * 订单金额
     */
    private BigDecimal order_amount;


    /**
     * 备注
     */
    private String remark;

    /**
     * 数据版本
     */
    private Integer dbversion;

    /**
     * 创建人id
     */
    private Long c_id;
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;
    private String u_name;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;
}

package com.xinyirun.scm.bean.system.vo.business.order;

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
 * 订单
 * </p>
 *
 * @author wwl
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOrderVo implements Serializable {


    private static final long serialVersionUID = 7401880123812752331L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 状态 0执行中 1已完成 -1作废
     */
    private String status;
    private String status_name;

    /**
     * 是否数量浮动管控
     */
    private Boolean over_inventory_policy;

    /**
     * 上浮百分比
     */
    private BigDecimal over_inventory_upper;

    /**
     * 下浮百分比
     */
    private BigDecimal over_inventory_lower;

    /**
     * 业务id
     */
    private Integer serial_id;

    /**
     * 业务类型
     */
    private String serial_type;

    /**
     * 业务类型
     */
    private String serial_type_name;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 订单明细编号
     */
    private String order_no;

    /**
     * 订单号
     */
    private String order_detail_no;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 单据类型
     */
    private String bill_type_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 客户/供应商名称
     */
    private String customer_name;

    /**
     * 采购/销售企业
     */
    private String owner_name;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
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
     * 物料明细
     */
    private List<BOrderGoodsVo> detailListData;

    /**
     * 类型
     */
    private String type;


    /**
     * 序号
     */
    private int idx;

    /**
     * b_in_order_goods的id
     */
    private Integer in_order_goods_id;

    private String contract_dtf;

    /**
     * 供应商
     */
    private Integer customer_id;

    /**
     * 供应商编码
     */
    private String customer_code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 业务板块ID
     */
    private Integer business_type_id;

    /**
     * 业务板块code
     */
    private String business_type_code;

    /**
     * 业务板块名称
     */
    private String business_type_name;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 规格code
     */
    private String sku_code;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 物料编码
     */
    private String goods_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格
     */
    private String spec;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 单位code
     */
    private String unit_code;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 单价(含税)
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 金额(含税)
     */
    private BigDecimal amount;

    /**
     * 税率
     */
    private BigDecimal rate;

    /**
     * 交货日期
     */
    private LocalDateTime delivery_date;

    /**
     * 交货方式(1-自提;2-物流)
     */
    private String delivery_type;

    /**
     * 交货方式(1-自提;2-物流)·
     */
    private String delivery_type_name;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

}

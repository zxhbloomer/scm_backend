package com.xinyirun.scm.bean.system.vo.business.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
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
 * 出库订单
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库订单", description = "出库订单")
public class BOutOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 5545103188724788197L;

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
     * 类型
     */
    private String type;

    /**
     * 序号
     */
    private int idx;

    /**
     * b_out_order_goods的id
     */
    private Integer out_order_goods_id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 订单明细编号
     */
    private String order_detail_no;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;
    private String contract_dtf;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 客户
     */
    private Integer customer_id;

    /**
     * 客户编码
     */
    private String customer_code;

    /**
     * 客户名称
     */
    private String customer_name;

    /**
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户编码
     */
    private String client_code;

    /**
     * 客户名称
     */
    private String client_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 货主名称
     */
    private String owner_name;

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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改人
     */
    private String u_name;

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
    private Integer num;

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
     * 物料明细
     */
    private List<BOutOrderGoodsVo> detailListData;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
    /**
     * 状态集合
     */
    private String[] status_list;

    /**
     * 物流订单数量
     */
    private Integer schedule_count;

    /**
     * 主键id集合
     */
    private List<String> ids;

    /**
     * 合同到期日期
     */
    private LocalDateTime contract_expire_dt;

    /**
     * 订单来源
     */
    private String source_type;

    /**
     * 已出库数量
     */
    private BigDecimal out_actual_count;

    /**
     * 业务启动日期
     */
    private String batch;
}

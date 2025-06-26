package com.xinyirun.scm.bean.system.vo.business.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库计划
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划", description = "出库计划")
public class BOutPlanVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -4378929673151401465L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 委托单号
     */
    private String plan_code;

    /**
     * 委托时间
     */
    private LocalDateTime plan_time;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 单据状态值
     */
    private String status_name;

    /**
     * 审核人id
     */
    private Integer auditor_id;

    /**
     * 审核人名
     */
    private String e_name;

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
     * 数据来源:  0:文件导入；1: 人工添加；2:平台推送;3担保存货
     */
    private Boolean data_sources;

    /**
     * 作废原因
     */
    private String invalid_reason;

    /**
     * 作废原因id
     */
    private Integer invalid_reason_id;

    /**
     * 责任归属：0 我方1 客户
     */
    private Boolean responsibility_owner;

    /**
     * 情况说明
     */
    private String information_note;

    /**
     * 是否同意:0否;1是
     */
    private Boolean is_agree;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 出库类型
     */
    private String type;

    /**
     * 出库类型值
     */
    private String type_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 创建时间起
     */
    private LocalDateTime start_time;

    /**
     * 创建时间止
     */
    private LocalDateTime over_time;

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
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 订单Id
     */
    private Integer order_id;

    /**
     * 订单类型
     */
    private String order_type;

    /**
     * 单号
     */
    private String code;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 计划单id
     */
    private Integer plan_id;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 物料名
     */
    private String goods_name;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料规格
     */
    private String spec;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 物料单价
     */
    private BigDecimal price;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 库存计量单位
     */
    private String unit;

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
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 委托方名
     */
    private String consignor_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 委托方编码
     */
    private String consignor_code;

    /**
     * 货主名
     */
    private String owner_name;

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
     * 可用库存
     */
    private BigDecimal qty_avaible;

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

    // 出库单bean字段-----------------------

    /**
     * 出库时间
     */
    private LocalDateTime outbound_time;

    /**
     * 实际数量
     */
    private BigDecimal actual_count;

    /**
     * 实际重量
     */
    private BigDecimal actual_weight;

    /**
     * 出库单详情list
     */
    private List<BOutPlanDetailVo> detailVo;
}

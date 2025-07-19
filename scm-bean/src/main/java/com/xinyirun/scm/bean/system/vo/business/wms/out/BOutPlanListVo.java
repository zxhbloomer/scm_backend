package com.xinyirun.scm.bean.system.vo.business.wms.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.annotations.bpm.FieldMeta;
import com.xinyirun.scm.common.constant.SystemConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库计划列表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划列表", description = "出库计划列表")
public class BOutPlanListVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6353894839023687369L;

    /**
     * 出库计划明细id
     */
    private Integer id;

    /**
     * 出库计划id
     */
    private Integer plan_id;

    /**
     * 出库单数量
     */
    private Integer out_counts;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 计划明细单号
     */
    private String code;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 委托时间
     */
    private LocalDateTime plan_time;

    /**
     * 单据状态
     */
    private String status;
    private String[] status_list;

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
    @FieldMeta(title = "出库类型", required = true, fieldType = SystemConstants.BPM_FORM.FIELD_TYPE_SELECTINPUT ,valueType = SystemConstants.BPM_FORM.VALUE_TYPE_STRING,
            dicType = "[{'key':'1','value':'出库'},{'key':'2','value':'收货'}]")
    private String type;

    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 别名
     */
    private String alias;

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
     * 物料名
     */
    private String goods_name;

    /**
     * 物料编码
     */
    private String goods_code;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 规格编码
     */
    private Integer sku_id;


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
     * 订单编号
     */
    private String order_no;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 订单类型
     */
    private String order_type;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 出库单位
     */
    private String unit_name;

    /**
     * 库存计量单位
     */
    private String unit;

    /**
     * 数量
     */
    @FieldMeta(title = "出库数量", required = true, fieldType = SystemConstants.BPM_FORM.FIELD_TYPE_NUMBERINPUT ,valueType = SystemConstants.BPM_FORM.VALUE_TYPE_NUMBER)
    private BigDecimal count;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 客户id
     */
    private Integer customer_id;

    /**
     * 客户名
     */
    private String customer_name;

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
     * 仓库类型
     */
    private String warehouse_type;

    /**
     * 待处理数量
     */
    private BigDecimal pending_count;

    /**
     * 待处理重量
     */
    private BigDecimal pending_weight;

    /**
     * 已处理(出/入)库数量
     */
    private BigDecimal has_handle_count;

    /**
     * 已处理(出/入)库重量
     */
    private BigDecimal has_handle_weight;


    /**
     * 岗位id集合
     */
    private List<Long> serial_ids;

    /**
     * 待办入库明细id集合
     */
    private Integer[] todo_ids;

    /**
     * 已办入库明细id集合
     */
    private Integer[] already_do_ids;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 0待办/1已办/2全部
     */
    private String todo_status;

    /**
     * 最大出库量
     */
    private BigDecimal max_count;

    /**
     * 作废备注
     */
    private String remark;

    /**
     * 调度单明细id
     */
    private Integer allocate_detail_id;

    /**
     * 作废备注
     */
    private String cancel_remark;

    /**
     * 同步状态
     */
    private String sync_status;

    /**
     * 放货指令编号
     */
    private String release_order_code;

    /**
     * 仓库类型
     */
    private String warehouse_type_name;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 总条数
     */
    private Integer total_count;

    /**
     * 作废审核人
     */
    private String cancel_audit_name;

    /**
     * 作废审核时间
     */
    private LocalDateTime cancel_audit_dt;

    /**
     * 退货数量
     */
    private BigDecimal return_qty;

    private String[] filter_type;

}

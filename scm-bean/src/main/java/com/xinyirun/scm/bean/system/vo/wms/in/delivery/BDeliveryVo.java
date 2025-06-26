package com.xinyirun.scm.bean.system.vo.wms.in.delivery;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 提货单
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDeliveryVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6968420136820447831L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 入库单号
     */
    private String code;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 入库计划单号
     */
    private Integer plan_id;

    /**
     * 入库计划明细id
     */
    private Integer plan_detail_id;

    /**
     * 入库计划单号
     */
    private String plan_code;


    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 计划单序号
     */
    private String plan_son_code;

    /**
     * 订单Id
     */
    private Integer order_id;

    /**
     * 订单类型
     */
    private String order_type;

    /**
     * 入库类型：0采购入库，1调拨入库，2退货入库，9监管入库，10普通入库
     */
    private String type;

    /**
     * 入库类型, 集合
     */
    private String[] type_list;

    /**
     * 入库类型值
     */
    private String type_name;

    /**
     * 入库状态：0制单，1已提交，2审核通过，3审核驳回，4已入库，5作废
     */
    private String status;
    private String[] status_list;

    /**
     * 入库状态值
     */
    private String status_name;

    /**
     * 是否已结算 0否 1是
     */
    private Boolean is_settled;

    /**
     * 结算单号
     */
    private String settle_code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 委托方编码
     */
    private String consignor_code;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 供应商id
     */
    private Integer supplier_id;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 供应商
     */
    private Integer customer_id;

    /**
     * 供应商编码
     */
    private String customer_code;

    /**
     * 供应商名称
     */
    private String customer_name;

    /**
     * 批次
     */
    private String lot;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库仓库id
     */
    private Integer warehouse_id;

    /**
     * 入库仓库code
     */
    private String warehouse_code;

    /**
     * 入库仓库类型
     */
    private String warehouse_type;

    /**
     * 入库库区id
     */
    private Integer location_id;

    /**
     * 入库库区code
     */
    private String location_code;

    /**
     * 入库库区值
     */
    private String location_name;

    /**
     * 入库库位id
     */
    private Integer bin_id;

    /**
     * 入库库位code
     */
    private String bin_code;

    /**
     * 入库库位值
     */
    private String bin_name;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 计划数量
     */
    private BigDecimal plan_count;

    /**
     * 计划重量
     */
    private BigDecimal plan_weight;

    /**
     * 计划体积
     */
    private BigDecimal plan_volume;

    /**
     * 实际数量
     */
    private BigDecimal actual_count;

    /**
     * 实际重量
     */
    private BigDecimal actual_weight;

    /**
     * 实际体积
     */
    private BigDecimal actual_volume;


    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;


    /**
     * 入库单位
     */
    private Integer unit_id;

    /**
     * 入库单位名
     */
    private String unit_name;

    /**
     * 入库计量单位
     */
    private String unit;

    /**
     * 换算单位信息
     */
    private MGoodsUnitCalcVo unitData;

    /**
     * 入库时间
     */
    private LocalDateTime inbound_time;

    /**
     * 入库时间起
     */
    private LocalDateTime start_time;

    /**
     * 入库时间止
     */
    private LocalDateTime over_time;

    /**
     * 收货确认单id
     */
    private Integer receive_order_id;

    /**
     * 库存流水id
     */
    private Integer inventory_account_id;

    /**
     * 审核人名
     */
    private String e_name;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核时间
     */
    private LocalDateTime e_dt;

    /**
     * 审核意见
     */
    private String e_opinion;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 委托方
     */
    private String consignor_name;

    /**
     * 货主
     */
    private String owner_name;

    /**
     * 仓库名
     */
    private String warehouse_name;

    /**
     * 仓库全名
     */
    private String warehouse_full_name;

    /**
     * 入库单id
     */
    private Integer in_id;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 规格
     */
    private String spec;

    /**
     * 物料名
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
     * 规格编码
     */
    private String sku_code;

    /**
     * 商品总价
     */
    private BigDecimal total_price;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 磅单文件
     */
    private Integer pound_file;

    /**
     * 照片
     */
    private Integer photo_file;

    /**
     * 检验单
     */
    private Integer inspection_file;

    /**
     * 物料明细表
     */
    private Integer goods_file;

    /**
     * 物料信息说明
     */
    private String info_detail;

    /**
     * 物料是否合格:0合格,1不合格
     */
    private Boolean stock_status;

    /**
     * 是否异常:0否;1:是
     */
    private Boolean is_exception;

    /**
     * 异常描述
     */
    private String exception_explain;

    /**
     * 原发数量
     */
    private BigDecimal primary_quantity;

    /**
     * 实收车数
     */
    private Integer car_count;

    /**
     * 磅单文件
     */
    private List<SFileInfoVo> pound_files;

    /**
     * 入库明细附件
     */
    private List<SFileInfoVo> photo_files;

    /**
     * 检验单
     */
    private List<SFileInfoVo> inspection_files;

    /**
     * 货物照片
     */
    private List<SFileInfoVo> goods_files;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 0待办/1已办/2全部
     */
    private String todo_status;

    /**
     * 作废备注
     */
    private String cancel_remark;

    /**
     * 审核时间
     */
    private String e_dt_start;

    /**
     * 审核时间
     */
    private String e_dt_end;

    /**
     * 商品属性
     */
    private String prop;

    /**
     * 物流合同
     */
    private String waybill_code;

    /**
     * 同步状态
     */
    private String sync_status;

    /**
     * 创建开始时间
     */
    private LocalDateTime c_time_start;

    /**
     * 创建结束时间
     */
    private LocalDateTime c_time_end;

    /**
     * 监管任务 monitor_in id
     */
    private Integer monitor_in_id;

    /**
     * 车牌数
     */
    private String vehicle_no;

    /**
     * 项目启动时间
     */
    private String batch;


    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 总条数
     */
    private Long total_count;

    /**
     * 作废审核人
     */
    private String cancel_audit_name;

    /**
     * 作废审核时间
     */
    private LocalDateTime cancel_audit_dt;

    /**
     * 是否去除稻壳杂质 1-是 2-否
     */
    private String remove_impurity;
}

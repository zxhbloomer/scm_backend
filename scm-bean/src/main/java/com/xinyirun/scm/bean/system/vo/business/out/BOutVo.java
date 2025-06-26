package com.xinyirun.scm.bean.system.vo.business.out;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库单
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库单", description = "出库单")
public class BOutVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 8885029232028167446L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 出库单号
     */
    private String code;

    /**
     * 序号
     */
    private Integer no;


    /**
     * 出库计划单号
     */
    private String plan_code;

    /**
     * 出库计划子单号
     */
    private String detail_code;

    /**
     * 出库类型:0退厂出库，1调拨出库，2维修出库，3借出出库,9监管出库，10普通出库
     */
    private String type;

    /**
     * 出库类型集合
     */
    private String[] type_list;

    /**
     * 是否已结算 0否 1是
     */
    private Boolean is_settled;

    /**
     * 出库状态：0制单，1已提交，2审核通过，3审核驳回，4已出库，5作废
     */
    private String status;
    private String[] status_list;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 委托人id
     */
    private Integer consignor_id;

    /**
     * 委托人编码
     */
    private String consignor_code;

    /**
     * 计划单id
     */
    private Integer plan_id;

    /**
     * 计划明细id
     */
    private Integer plan_detail_id;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户 code
     */
    private String client_code;


    /**
     * 委托方
     */
    private String consignor_name;

    /**
     * 货主
     */
    private String owner_name;

    /**
     * 客户名
     */
    private String client_name;

    /**
     * 仓库名
     */
    private String warehouse_name;

    /**
     * 仓库全称
     */
    private String warehouse_full_name;

    /**
     * 仓库类型
     */
    private String warehouse_type;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 单据类型值
     */
    private String bill_type_name;

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
     * 出库单位
     */
    private String convert_unit;

    /**
     * 出库计量单位
     */
    private String unit;

    /**
     * 单位换算
     */
    private BigDecimal calc;

    /**
     * 出库时间
     */
    private LocalDateTime outbound_time;

    /**
     * 出库时间起
     */
    private LocalDateTime start_time;

    /**
     * 出库时间止
     */
    private LocalDateTime over_time;

    /**
     * 入库库位id
     */
    private Integer warehouse_id;

    /**
     * 出库库位id
     */
    private Integer location_id;

    /**
     * 入库库位id
     */
    private Integer bin_id;

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
    private String goods_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料规格
     */
    private String spec;

    /**
     * 备注
     */
    private String remark;

    /**
     * 单据状态值
     */
    private String status_name;

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
     * 库存流水id
     */
    private Integer inventory_account_id;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核人名
     */
    private String e_name;

    /**
     * 审核意见
     */
    private String e_opinion;

    /**
     * 审核时间
     */
    private LocalDateTime e_dt;

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
    private Integer c_id;

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
     * 出库单位
     */
    private String unit_name;

    /**
     * 出库单位id
     */
    private Integer unit_id;

    /**
     * 出库单id
     */
    private Integer out_id;

    /**
     * 是否异常 0否 1是
     */
    private Boolean is_exception;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 异常描述
     */
    private String exceptionexplain;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

    /**
     * 磅单文件主表id
     */
    private Integer pound_file;

    /**
     * 出库照片附件主表id
     */
    private Integer out_photo_file;

    /**
     * 磅单文件
     */
    private List<SFileInfoVo> pound_files;

    /**
     * 出库照片附件
     */
    private List<SFileInfoVo> out_photo_files;

    /**
     * 换算单位信息
     */
    private MGoodsUnitCalcVo unitData;

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
    private String ed_dt_start;

    /**
     * 审核时间
     */
    private String ed_dt_end;

    /**
     * 商品属性
     */
    private String prop;

    /**
     * 同步状态
     */
    private String sync_status;

    /**
     * 放货指令编号
     */
    private String release_order_code;

    /**
     * 创建开始时间
     */
    private LocalDateTime c_time_start;

    /**
     * 创建结束时间
     */
    private LocalDateTime c_time_end;

    private Integer order_id;

    private String order_type;

    /**
     * 监管任务生产的出库单
     */
    private Integer monitor_out_id;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 启动日期
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
     * 可用库存
     */
    private BigDecimal qty_avaible;

    /**
     * 作废审核人
     */
    private String cancel_audit_name;

    /**
     * 作废审核时间
     */
    private LocalDateTime cancel_audit_dt;

    /**
     * 是否包含放货指令
     */
    private String out_release_status;

    /**
     * 换算后单位id
     */
    private Integer tgt_unit_id;

    /**
     * 换算前
     */
    private String src_unit;

    /**
     * 换算后
     */
    private String tgt_unit;

    /**
     * 退货数量
     */
    private BigDecimal return_qty;

    /**
     * 扣减退货的真实数量
     */
    private BigDecimal actual_count_return;

    /**
     * 扣减退货的真实重量
     */
    private BigDecimal actual_weight_return;
}

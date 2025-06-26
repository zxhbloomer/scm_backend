package com.xinyirun.scm.bean.system.vo.business.allocate;

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
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存调拨", description = "库存调拨")
public class BAllocateVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = 8812332734048393252L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 调整单号
     */
    private String code;

    /**
     * 序号
     */
    private Integer idx;

    /**
     * 是否自动生成入出库单 0否 1是
     */
    private Boolean auto;

    /**
     * 状态
     */
    private String status_name;

    /**
     * 调出货主id
     */
    private Integer out_owner_id;

    /**
     * 调出货主code
     */
    private String out_owner_code;

    /**
     * 调出货主
     */
    private String out_owner_name;

    /**
     * 调出委托方id
     */
    private Integer out_consignor_id;

    /**
     * 调出委托方code
     */
    private String out_consignor_code;

    /**
     * 调出委托方
     */
    private String out_consignor_name;

    /**
     * 调入货主id
     */
    private Integer in_owner_id;

    /**
     * 调入货主code
     */
    private String in_owner_code;

    /**
     * 调入货主
     */
    private String in_owner_name;

    /**
     * 调入委托方id
     */
    private Integer in_consignor_id;

    /**
     * 调入委托方code
     */
    private String in_consignor_code;

    /**
     * 调入委托方
     */
    private String in_consignor_name;

    /**
     * 委托方code
     */
    private String consignor_code;

    /**
     * 委托方
     */
    private String consignor_name;

    /**
     * 调入仓库id
     */
    private Integer in_warehouse_id;

    /**
     * 调入仓库code
     */
    private String in_warehouse_code;

    /**
     * 调入仓库
     */
    private String in_warehouse_name;

    /**
     * 调出仓库id
     */
    private Integer out_warehouse_id;

    /**
     * 调出仓库code
     */
    private String out_warehouse_code;

    /**
     * 调出仓库
     */
    private String out_warehouse_name;

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
     * 数量
     */
    private BigDecimal qty;

    /**
     * 调拨订单id
     */
    private Integer order_id;

    /**
     * 调拨订单编号
     */
    private String order_no;

    /**
     * 调拨订单合同号
     */
    private String contract_no;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核人名称
     */
    private String e_name;

    /**
     * 审核时间
     */
    private LocalDateTime e_dt;

    /**
     * 调拨日期
     */
    private LocalDateTime allocate_time;

    /**
     * 备注
     */
    private String remark;

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
     * 修改人id
     */
    private Integer u_id;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 调拨单明细集合
     */
    private List<BAllocateDetailVo> detailList;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 0待办/1已办/2全部
     */
    private String todo_status;

    /**
     * 员工id
     */
    private Long staff_id;

    private String status;

    private LocalDateTime start_time;

    private LocalDateTime over_time;
}

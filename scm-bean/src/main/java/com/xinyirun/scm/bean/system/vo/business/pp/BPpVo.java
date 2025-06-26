package com.xinyirun.scm.bean.system.vo.business.pp;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoProductVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 生产计划表
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BPpVo extends BaseVo implements Serializable {

    
    private static final long serialVersionUID = -1545833262616832451L;

    /**
     * 主键
     */
    private Integer id;

    private Integer no;
    /**
     * 业务单号
     */
    private String code;

    /**
     * 状态：0制单，1已提交，2审核通过，3审核驳回，4作废，5作废审核中，6已完成
     */
    private String status;

    /**
     * 上一个状态：0制单，1已提交，2审核通过，3审核驳回，4作废，5作废审核中，6已完成
     */
    private String pre_status;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主id
     */
    private String owner_name;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区code
     */
    private String location_code;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位code
     */
    private String bin_code;

    /**
     * 计划开始入库时间
     */
    private LocalDateTime plan_time;

    /**
     * 计划结束入库时间
     */
    private LocalDateTime plan_end_time;

    /**
     * 放货指令id
     */
    private Integer release_order_id;

    /**
     * 放货指令code
     */
    private String release_order_code;

    /**
     * 审核人id
     */
    private Integer audit_id;

    /**
     * 审核人名字
     */
    private String audit_name;

    /**
     * 审核时间
     */
    private LocalDateTime audit_time;

    /**
     * 作废审核人id
     */
    private Integer cancel_audit_id;

    /**
     * 作废审核时间
     */
    private LocalDateTime cancel_audit_time;

    /**
     * 原材料json
     */
    private String json_material_list;

    /**
     * 产成品、副产品json
     */
    private String json_product_list;

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
     * 创建人名字
     */
    private String c_name;

    /**
     *状态名字
     */
    private String status_name;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 创建人名字
     */
    private String u_name;

    /**
     * 原材料 列表
     */
    private List<BPpMaterialVo> material_list;

    /**
     * 产成品, 副产品列表
     */
    private List<BPpProductVo> product_list;

    /**
     * 副产品列表
     */
    private List<BPpProductVo> coproduct_list;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * id 集合
     */
    private Integer[] ids;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 0待办/1已办/2全部
     */
    private String todo_status;

    /**
     *配方id
     */
    private Integer router_id;

    /**
     * 配方编号
     */
    private String router_code;

    /**
     * 配方名称
     */
    private String router_name;

    /**
     * 作废理由
     */
    private String remark;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * 产成品, 副产品商品名称或编码
     */
    private String product_goods_name;

    /**
     * 原材料 名称或编码
     */
    private String material_goods_name;

    /**
     * 生产物料编码
     */
    private String release_sku_code;

    private String release_sku_name;

    private String release_pm;
    private String release_type_gauge;
    private String release_qty;
    private String release_unit_name;
    private String release_spec;
    private BigDecimal has_product_num = BigDecimal.ZERO;

    /**
     * 类型名称
     */
    private String release_order_type_name;

    private Integer release_order_detail_id;

    /**
     * 产成品, 副产品合计 (计划生产数量)
     */
    private BigDecimal product_actual;

    /**
     * 原材料合计 (计划领取材料数量)
     */
    private BigDecimal material_actual;

    /**
     * 产成品, 副产品合计  (已生产数量)
     */
    private BigDecimal product_actual_wo;

    /**
     * 原材料合计 (已领取材料数量)
     */
    private BigDecimal material_actual_wo;


    /**
     * 待生产数量
     */
    private BigDecimal product_actual_wait;

    /**
     *待领取材料数量
     */
    private BigDecimal material_actual_wait;

    /**
     *生产订单数量
     */
    private Integer bwo_sum;

    private String[] status_list;

    /**
     *仓库名字
     */
    private String warehouse_name;

}

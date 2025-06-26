package com.xinyirun.scm.bean.system.vo.business.rtwo;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  生产管理 表
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BRtWoVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1721787044437958407L;

    private Integer id;

    /**
     * 生产 管理, 编号
     */
    private String code;

    private String type;

    /**
     * 配方表 id
     */
    private Integer router_id;

    /**
     * 货主 id
     */
    private Integer owner_id;

    /**
     * 仓库 id
     */
    private Integer wc_warehouse_id;

    /**
     * 仓库 编码
     */
    private String wc_warehouse_code;

    /**
     * 库区 id
     */
    private Integer wc_location_id;

    /**
     * 库区 编码
     */
    private String wc_location_code;

    /**
     * 货位 id
     */
    private Integer wc_bin_id;

    /**
     * 货位 编码
     */
    private String wc_bin_code;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String status_name;

    private LocalDateTime wo_dt;

    private Integer delivery_order_id;

    private String delivery_order_code;

    private Integer delivery_order_detail_id;

    private Integer delivery_order_detail_no;

    private String delivery_order_detail_sku_code;

    private Integer delivery_order_detail_qty;

    /**
     * 类型
     */
    private String delivery_order_type_name;

    /**
     * 货主
     */
    private String owner_name;

    private String wc_warehouse_name;

    /**
     * 生产物料编码
     */
    private String delivery_sku_code;

    /**
     * 生产物料名称
     */
    private String delivery_sku_name;

    /**
     * 品名
     */
     private String pm;

    /**
     * 规格
     */
    private String delivery_spec;

    /**
     * 行规
     */
    private String delivery_type_gauge;

    /**
     * 数量
     */
    private BigDecimal delivery_qty;

    /**
     * 单位
     */
    private String delivery_unit_name;
    


    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 审核时间
     */
    private LocalDateTime e_time;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    private String u_name;

    private String e_name;

    private String c_name;

    private Integer e_id;

    private Integer dbversion;

    /**
     * 原材料 列表
     */
    private List<BRtWoMaterialVo> material_list;

    /**
     * 产成品列表
     */
    private List<BRtWoProductVo> product_list;

    /**
     * 副产品列表
     */
    private List<BRtWoProductVo> coproduct_list;
    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * 已生成数量
     */
    private BigDecimal has_product_num;

    /**
     * 产成品, 副产品商品名称或编码
     */
    private String product_goods_name;

    /**
     * 原材料 名称或编码
     */
    private String material_goods_name;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 产成品, 副产品合计
     */
    private BigDecimal product_actual;

    /**
     * 原材料合计
     */
    private BigDecimal material_actual;

    /**
     * 作废理由
     */
    private String remark;

    /**
     * 导出 id
     */
    private Integer[] ids;

    private Integer no;

    /**
     * 产成品
     */
    private String json_product_list;

    /**
     * 副产品
     */
    private String json_coproduct_list;

    /**
     * 原材料
     */
    private String json_material_list;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 0待办/1已办/2全部
     */
    private String todo_status;

    /**
     * 业务启动日期
     */
    private String batch;
}

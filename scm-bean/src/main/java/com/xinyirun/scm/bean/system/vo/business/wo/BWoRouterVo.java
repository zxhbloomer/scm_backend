package com.xinyirun.scm.bean.system.vo.business.wo;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: Wang Qianfeng
 * @DATE: 2022/12/22 : 16:31
 * @Description:
 **/
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BWoRouterVo implements Serializable {

    private static final long serialVersionUID = 2125957642277894297L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 类型
     */
    private String type;

    /**
     * 名字
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 1启用, 0禁用
     */
    private Boolean is_enable;
    private String enable_name;

    /**
     * 产成品, 副产品
     */
    private List<BWoRouterProductVo> product_list;

    // ======= 产成品, 副产品, 暂时分页时用, 不用请删除 ==============
    /**
     * 类型, 产成品, 副产品
     */
    private String product_type_name;

    /**
     * 商品code
     */
    private String product_sku_code;

    /**
     * 物料名称
     */
    private String product_goods_name;

    /**
     * 品名
     */
    private String product_pm;

    /**
     * 规格
     */
    private String product_spec;

    /**
     * 型规
     */
    private String product_type_gauge;

    /**
     * 型规
     */
    private String product_goods_prop;

    /**
     * 配比
     */
    private BigDecimal product_router;

    /**
     * 原材料
     */
    private List<BWoRouterMaterialVo> material_list;

    // ---  原材料, 暂时分页时用, 不用请删除  ----------
    private String material_sku_code;

    /**
     * 物料名称
     */
    private String material_goods_name;

    /**
     * 品名
     */
    private String material_pm;

    /**
     * 规格
     */
    private String material_spec;

    /**
     * 型规
     */
    private String material_type_gauge;

    /**
     * 商品属性
     */
    private String material_goods_prop;

    /**
     * 配比
     */
    private BigDecimal material_router;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    /**
     * 数据版本
     */
    private Integer dbversion;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 开始时间
     */
    private LocalDateTime over_time;
}

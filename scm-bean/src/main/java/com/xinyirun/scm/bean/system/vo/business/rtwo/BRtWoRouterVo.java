package com.xinyirun.scm.bean.system.vo.business.rtwo;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public class BRtWoRouterVo implements Serializable {

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
     * 产成品集合
     */
    private List<BRtWoRouterProductVo> product_list;

    /**
     * 副产品集合
     */
    private List<BRtWoRouterProductVo> coproduct_list;

    /**
     * 原材料
     */
    private List<BRtWoRouterMaterialVo> material_list;

    /**
     * 产成品, 副产品名称
     */
    private String product_goods_name;

    /**
     * 原材料名称
     */
    private String material_goods_name;

    /**
     * 产成品 商品编码
     */
    private String product_sku_code;

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

package com.xinyirun.scm.bean.system.vo.business.materialconvert;

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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialConvertPriceVo implements Serializable {


    private static final long serialVersionUID = 2803560609060789404L;
    /**
     * 编码
     */
    private String code;

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
     * 货主简称
     */
    private String owner_simple_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库简称
     */
    private String warehouse_simple_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区名称
     */
    private String location_name;


    /**
     * 库区简称
     */
    private String location_simple_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位名称
     */
    private String bin_name;


    /**
     * 库位简称
     */
    private String bin_simple_name;

    /**
     * 板块id
     */
    private Integer business_id;

    /**
     * 板块code
     */
    private String business_code;

    /**
     * 板块名称
     */
    private String business_name;

    /**
     * 行业ID
     */
    private Integer industry_id;

    /**
     * 行业code
     */
    private String industry_code;

    /**
     * 行业名称
     */
    private String industry_name;

    /**
     * 类别id
     */
    private Integer category_id;

    /**
     * 类别code
     */
    private String category_code;

    /**
     * 类别名称
     */
    private String category_name;

    /**
     * 商品id
     */
    private Integer goods_id;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 商品code
     */
    private String goods_code;

    /**
     * 物料规格id
     */
    private Integer sku_id;

    /**
     * 物料规格编码
     */
    private String sku_code;

    /**
     * 物料规格名称
     */
    private String sku_name;

    /**
     * 15天累计到货量
     */
    private BigDecimal qty;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 15天累计货值
     */
    private BigDecimal amount;

    /**
     * 生成时间
     */
    private LocalDateTime c_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;


    /**
     * 日期
     */
    private String dt;


    /**
     * 商品属性id
     */
    private Integer prop_id;

    /**
     * 商品属性名称
     */
    private String prop_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 规格
     */
    private String spec;

    /**
     * 唯一主键
     */
    private String id;

    /**
     * id 集合
     */
    private String[] ids;

}

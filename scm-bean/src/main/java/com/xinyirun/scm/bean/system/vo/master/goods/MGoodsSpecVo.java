package com.xinyirun.scm.bean.system.vo.master.goods;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 物料规格
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "物料规格", description = "物料规格")
public class MGoodsSpecVo implements Serializable {

    private static final long serialVersionUID = 9210763526984183692L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 物料编码
     */
    private String code;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 物料编码
     */
    private String goods_code;

    /**
     * 名称
     */
    private String name;

    /**
     * 物料属性id
     */
    private Integer prop_id;

    /**
     * 物料属性
     */
    private String prop_name;

    /**
     * 物料属性code
     */
    private String prop_code;

    /**
     * 单位(米/支、码/支)
     */
    private String unit;

    /**
     * 规格
     */
    private String spec;

    /**
     * 是否启用（1是0否）
     */
    private Boolean enable;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 净重
     */
    private BigDecimal net_weight;

    /**
     * 毛重
     */
    private BigDecimal rough_weight;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 产地
     */
    private String orgin;

    /**
     * 板块id
     */
    private Integer business_id;

    /**
     * 板块名称
     */
    private String business_name;

    /**
     * 行业ID
     */
    private Integer industry_id;

    /**
     * 行业名称
     */
    private String industry_name;

    /**
     * 类别id
     */
    private Integer category_id;

    /**
     * 类别名称
     */
    private String category_name;

    /**
     * 物料id
     */
    private Integer goods_id;

    /**
     * 品名
     */
    private String pm;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 查询关键词
     */
    private String keyword;

    /**
     * 修改人姓名
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
     * 单位换算集合
     */
    private List<MGoodsUnitConvertVo> unitList;

    private Integer[] ids;

    /**
     * 转换类型, before , after
     */
    private Integer covert_type;

    /**
     * 转换选择的货主
     */
    private Integer owner_id;

}

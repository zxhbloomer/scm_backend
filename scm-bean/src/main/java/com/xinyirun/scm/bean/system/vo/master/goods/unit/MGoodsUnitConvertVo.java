package com.xinyirun.scm.bean.system.vo.master.goods.unit;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 单位换算
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "单位换算", description = "单位换算")
public class MGoodsUnitConvertVo implements Serializable {

    private static final long serialVersionUID = 1305185167406914508L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 物料规格id
     */
    private Integer sku_id;
    /**
     * 计量单位id
     */
    private Integer jl_unit_id;

    /**
     * 计量单位
     */
    private String jl_unit;

    /**
     * 换算单位id
     */
    private Integer hs_unit_id;

    /**
     * 换算单位
     */
        private String hs_unit;

    /**
     * 换算关系
     */
    private BigDecimal hs_gx;

    /**
     * 是否删除
     */
    private Boolean enable;

    /**
     * 顺序
     */
    private String idx;

    /**
     * 描述
     */
    private String remark;

    /**
     * 描述
     */
    private String des;


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


}

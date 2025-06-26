package com.xinyirun.scm.bean.system.vo.master.goods.unit;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 单位换算vo
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "单位换算vo", description = "单位换算vo")
public class MGoodsUnitCalcVo implements Serializable {

    private static final long serialVersionUID = -1302435616983944021L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 物料规格id
     */
    private Integer sku_id;

    /**
     * 原计量单位id
     */
    private Integer src_unit_id;

    /**
     * 原计量单位code
     */
    private String src_unit_code;

    /**
     * 原计量单位
     */
    private String src_unit;

    /**
     * 换算后单位id
     */
    private Integer tgt_unit_id;

    /**
     * 换算后单位code
     */
    private String tgt_unit_code;

    /**
     * 换算后单位
     */
    private String tgt_unit;

    /**
     * 换算关系
     */
    private BigDecimal calc;

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
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 换算内容
     */
    private String content;

    /**
     * 更新状态
     */
    private String status;
}

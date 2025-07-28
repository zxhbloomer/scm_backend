package com.xinyirun.scm.bean.entity.business.wo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 生产配方_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wo_router_product")
public class BWoRouterProductEntity implements Serializable {

    private static final long serialVersionUID = -257085963491040285L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    /**
     * 0 产成品, 1副产品
     */
    @TableField("type")
    private String type;

    @TableField("router_id")
    private Integer router_id;

    @TableField("sku_id")
    private Integer sku_id;

    @TableField("sku_code")
    private String sku_code;

    @TableField("router")
    private BigDecimal router;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

}

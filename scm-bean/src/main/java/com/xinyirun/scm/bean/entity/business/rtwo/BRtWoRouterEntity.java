package com.xinyirun.scm.bean.entity.business.rtwo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 生产配方
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_rt_wo_router")
public class BRtWoRouterEntity implements Serializable {

    private static final long serialVersionUID = -6789528244565946999L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    @TableField("type")
    private String type;

    @TableField("name")
    private String name;

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

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 是否启用
     */
    @TableField("is_enable")
    private String is_enable;

    /**
     * 原材料json
     */
    @TableField("json_material_list")
    private String json_material_list;

    /**
     * 产成品、副产品json
     */
    @TableField("json_product_list")
    private String json_product_list;

    /**
     * 产成品、副产品json
     */
    @TableField("json_coproduct_list")
    private String json_coproduct_list;

}

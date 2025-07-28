package com.xinyirun.scm.bean.entity.business.ownerchange;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_owner_change")
public class BOwnerChangeEntity implements Serializable {

    private static final long serialVersionUID = -1726505171062601836L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 调拨单号
     */
    @TableField("code")
    private String code;

    /**
     * 原货主id
     */
    @TableField("out_owner_id")
    private Integer out_owner_id;

    /**
     * 原货主code
     */
    @TableField("out_owner_code")
    private String out_owner_code;

    /**
     * 原委托方id
     */
    @TableField("out_consignor_id")
    private Integer out_consignor_id;

    /**
     * 原委托方code
     */
    @TableField("out_consignor_code")
    private String out_consignor_code;

    /**
     * 新货主id
     */
    @TableField("in_owner_id")
    private Integer in_owner_id;

    /**
     * 新货主code
     */
    @TableField("in_owner_code")
    private String in_owner_code;

    /**
     * 新委托方id
     */
    @TableField("in_consignor_id")
    private Integer in_consignor_id;

    /**
     * 新委托方code
     */
    @TableField("in_consignor_code")
    private String in_consignor_code;

    /**
     * 新仓库id
     */
    @TableField("in_warehouse_id")
    private Integer in_warehouse_id;

    /**
     * 新仓库code
     */
    @TableField("in_warehouse_code")
    private String in_warehouse_code;

    /**
     * 原仓库id
     */
    @TableField("out_warehouse_id")
    private Integer out_warehouse_id;

    /**
     * 原仓库code
     */
    @TableField("out_warehouse_code")
    private String out_warehouse_code;

    /**
     * 订单id
     */
    @TableField(value="order_id")
    private Integer order_id;

    /**
     * 转移日期
     */
    @TableField(value="change_time")
    private LocalDateTime change_time;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 附件信息
     */
    @TableField("files")
    private Integer files;

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
    @TableField(value="dbversion")
    private Integer dbversion;


}

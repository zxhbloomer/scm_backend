package com.xinyirun.scm.bean.entity.business.so.arreceive;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应收来源表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_receive_source")
public class BArReceiveSourceEntity implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 7924681357462039184L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收主表id
     */
    @TableField("ar_receive_id")
    private Integer ar_receive_id;

    /**
     * 应收主表code
     */
    @TableField("ar_receive_code")
    private String ar_receive_code;

    /**
     * 应收账款主表id
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    @TableField("ar_code")
    private String ar_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    @TableField("type")
    private String type;

    /**
     * 项目编号
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    private String so_contract_code;

    /**
     * 销售订单id
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    private String so_order_code;
}
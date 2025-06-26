package com.xinyirun.scm.bean.entity.busniess.schedule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 调度
 * </p>
 *
 * @author wwl
 * @since 2022-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_schedule_info")
public class BScheduleInfoEntity implements Serializable {

    private static final long serialVersionUID = -7346987509576865798L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物流订单id
     */
    @TableField("schedule_id")
    private Integer schedule_id;

    /**
     * 物流合同号
     */
    @TableField("waybill_contract_no")
    private String waybill_contract_no;

    /**
     * 承运商id
     */
    @TableField("customer_id")
    private Integer customer_id;

    /**
     * 承运商名称
     */
    @TableField("customer_name")
    private String customer_name;

    /**
     * 承运商code
     */
    @TableField("customer_code")
    private String customer_code;

}

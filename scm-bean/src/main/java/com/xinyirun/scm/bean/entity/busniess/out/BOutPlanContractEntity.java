package com.xinyirun.scm.bean.entity.busniess.out;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划合同
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_out_plan_contract")
public class BOutPlanContractEntity implements Serializable {

    private static final long serialVersionUID = 3512523860186703295L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 计划单id
     */
    @TableField("plan_id")
    private Integer plan_id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String order_no;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 船名
     */
    @TableField("ship_name")
    private String ship_name;

    /**
     * 合同日期
     */
    @TableField("contract_dt")
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    @TableField("contract_num")
    private BigDecimal contract_num;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Integer client_id;

    /**
     * 客户编码
     */
    @TableField("client_code")
    private String client_code;
}

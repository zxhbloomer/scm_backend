package com.xinyirun.scm.bean.system.vo.business.schedule;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库订单
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "调度订单", description = "调度订单")
public class BScheduleOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -6322798772598797823L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 单据类型:0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户编码
     */
    private String client_code;

    /**
     * 供应商id
     */
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 客户名称
     */
    private String client_name;

    /**
     * 供应商名称
     */
    private String supplier_name;

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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}

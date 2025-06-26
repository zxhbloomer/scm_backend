package com.xinyirun.scm.bean.system.vo.business.adjust;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调整订单
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存调整订单", description = "库存调整订单")
public class BAdjustOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7955473379741834628L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 供应商
     */
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    private String supplier_code;

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
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;


}

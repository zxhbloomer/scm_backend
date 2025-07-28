package com.xinyirun.scm.bean.system.vo.business.so.arreceive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 应收来源表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReceiveSourceVo implements Serializable {
    
    @Serial
    private static final long serialVersionUID = -8491732856174029385L;
    
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 应收主表id
     */
    private Integer ar_receive_id;

    /**
     * 应收主表code
     */
    private String ar_receive_code;

    /**
     * 应收账款主表id
     */
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    private String ar_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    private String type;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    private String so_order_code;
}
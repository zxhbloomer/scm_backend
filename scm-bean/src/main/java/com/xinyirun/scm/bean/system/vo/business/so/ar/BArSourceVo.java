package com.xinyirun.scm.bean.system.vo.business.so.ar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 应收账款关联单据表-源单
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArSourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1325791178665355609L;

    private Integer id;

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
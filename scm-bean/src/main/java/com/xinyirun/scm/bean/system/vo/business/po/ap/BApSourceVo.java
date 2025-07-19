package com.xinyirun.scm.bean.system.vo.business.po.ap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 应付账款关联单据表-源单
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApSourceVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 8207964110392763681L;

    private Integer id;

    /**
     * 应付账款主表id
     */
    private Integer ap_id;

    /**
     * 应付账款主表code
     */
    private String ap_code;

    /**
     * 1-应付、2-预付、3-其他支出
     */
    private String type;

    /**
     * 采购合同id
     */
    private Integer po_contract_id;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单id
     */
    private Integer po_order_id;

    /**
     * 采购订单编号
     */
    private String po_order_code;

    /**
     * 付款主表id
     */
    private Integer ap_pay_id;

    /**
     * 付款主表code
     */
    private String ap_pay_code;

}

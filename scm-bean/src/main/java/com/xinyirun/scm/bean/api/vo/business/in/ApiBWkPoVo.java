package com.xinyirun.scm.bean.api.vo.business.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库订单
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBWkPoVo implements Serializable {

    private static final long serialVersionUID = -4050971413537083375L;
    /**
     * 主键id
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
     * 供应商
     */
    private Integer supplier_id;

    /**
     * 供应商信用代码
     */
    private String supplier_credit_no;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主信用代码
     */
    private String owner_credit_no;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 业务板块ID
     */
    private Integer business_type_id;

    /**
     * 业务板块code
     */
    private String business_type_code;

    /**
     * 业务板块名称
     */
    private String business_type_name;

    /**
     * 错误类型 1订单编号为空 2业务类型为空 3供应商位同步 4货主未同步 5业务板块未同步
     */
    private String flag;


}

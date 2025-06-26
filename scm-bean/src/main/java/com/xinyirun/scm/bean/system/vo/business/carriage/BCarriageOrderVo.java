package com.xinyirun.scm.bean.system.vo.business.carriage;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BCarriageOrderVo implements Serializable {

    private static final long serialVersionUID = -5038867735300910818L;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    /**
     * 主键 id
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 承运合同编号
     */
    private String carriage_contract_code;

    /**
     * 状态
     */
    private String status;

    /**
     * 合同类型名称
     */
    private String type_name;

    /**
     * 承运人企业名称
     */
    private String company_name;

    /**
     * 承运人企业id
     */
    private Integer company_id;

    /**
     * 承运人企业code
     */
    private String company_code;

    /**
     * 承运人企业信用代码
     */
    private String company_credit_no;

    /**
     * 托运人组织主体名称
     */
    private String org_name;

    /**
     * 托运人组织主体信用代码
     */
    private String org_credit_no;

    /**
     * 承运订单备注
     */
    private String remark;

    /**
     * 商品 编码
     */
    private String sku_code;

    /**
     * 商品名称
     */
    private String sku_name;

    /**
     * 商品单位
     */
    private String unit_name;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品数量
     */
    private BigDecimal num;

    /**
     * 货值总金额
     */
    private BigDecimal amount;

    /**
     * 总货值
     */
    private BigDecimal total_amount;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 起始地
     */
    private String origin_place;

    /**
     * 目的地
     */
    private String destination_place;

    /**
     * 运输方式
     */
    private String transport_type_name;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * 运费总金额
     */
    private BigDecimal transport_amount;

    /**
     * 总货值
     */
    private BigDecimal transport_amount_tax;

    /**
     * 主键 ids
     */
    private Integer[] ids;

    /**
     * 签订日期
     */
    private String sign_dt;

    /**
     * 到期日期
     */
    private String deadline_dt;

    /**
     * 销售合同号
     */
    private String sales_contract_code;

    /**
     * 运距
     */
    private BigDecimal haul_distance;

    /**
     * 付款方式
     */
    private String pay_type;
}

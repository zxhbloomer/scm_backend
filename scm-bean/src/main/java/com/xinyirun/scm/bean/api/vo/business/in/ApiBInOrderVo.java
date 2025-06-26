package com.xinyirun.scm.bean.api.vo.business.in;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 入库计划合同
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBInOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 3192600981044251458L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 状态 0执行中 1已完成 -1作废
     */
    private String status;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 订单id
     */
    private Integer order_id;

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
     * 合同id
     */
    private Integer contract_id;

    /**
     * 运输方式id
     */
    private Integer mode_transport_id;

    /**
     * 运输方式
     */
    private String mode_transport_name;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同截至日期
     */
    private LocalDateTime contract_expire_dt;

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
     * 货主编码
     */
    private String owner_code;

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
     * 是否数量浮动管控
     */
    private Boolean float_controled;

    /**
     * 上浮百分比
     */
    private BigDecimal float_up;

    /**
     * 下浮百分比
     */
    private BigDecimal float_down;

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
     * 物料明细
     */
    private List<ApiBInOrderGoodsVo> detailListData;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}

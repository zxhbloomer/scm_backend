package com.xinyirun.scm.bean.api.vo.business.out;

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
 * 出库订单
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBOutOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 5545103188724788197L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 状态 0执行中 1正常 -1停用
     */
    private String status;

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
     * 客户id
     */
    private Integer client_id;

    /**
     * 客户编码
     */
    private String client_code;

    /**
     * 客户信用代码证
     */
    private String client_credit_no;

    /**
     * 客户名称
     */
    private String client_name;

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
    private List<ApiBOutOrderGoodsVo> detailListData;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
